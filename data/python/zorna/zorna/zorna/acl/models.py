import inspect
from django.core.cache import cache
from django.conf import settings
from django.contrib.auth.models import User, AnonymousUser
from django.db import models
from django.contrib.contenttypes import generic
from django.db.models.base import Model, ModelBase
from django.contrib.contenttypes.models import ContentType
from django.utils.translation import ugettext_lazy as _
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import Http404, HttpResponseRedirect
from django.db.models import Q
from django.core.paginator import Paginator, InvalidPage
from django.template.loader import render_to_string

from zorna.account.models import UserGroup
from zorna import defines

ACL_USERS_PERMISSIONS_CACHE = u"acl_users_permissions_cache"
ACL_GROUPS_PERMISSIONS_CACHE = u"acl_groups_permissions_cache"
ACL_MODEL_CACHE = u"acl_model_cache"


def get_acl_for_model(object):
    """
    return a permission class
    object can be an id, an instance or a class
    """
    if inspect.isclass(object):
        model = object
    else:
        model = object.__class__

    permclass = type('ObjectPermission', (BaseACL,), {'model': model})
    return permclass()


def register_acl_for_model(model, verbs):
    content_type = ContentType.objects.get_for_model(model)
    amc = cache.get(ACL_MODEL_CACHE)
    if amc is None:
        amc = {}
    try:
        return amc[content_type.pk]
    except:
        perms = []
        for k, v in verbs.iteritems():
            try:
                perm = ACLVerbPermission.objects.get(
                    codename=k,
                    content_type=content_type)
            except ACLVerbPermission.DoesNotExist:
                perm = ACLVerbPermission.objects.create(
                    name=v,
                    content_type=content_type,
                    codename=k)
            perms.append(perm)
        amc[content_type.pk] = perms
        cache.set(ACL_MODEL_CACHE, amc)
    return amc[content_type.pk]


def get_allowed_objects(user, model, permission):
    if type(permission) is list:
        ao = set([])
        for perm in permission:
            ao = ao.union(set(ACLPermission.objects.get_acl_objects_by_model(
                user, model, perm)))
        return list(ao)
    else:
        return ACLPermission.objects.get_acl_objects_by_model(user, model, permission)


def get_acl_by_object(object, permission):
    """
    return list of users that have access (explicitly or from groups ) to object on "permission" permission
    """
    return ACLPermission.objects.get_acl_by_object(object, permission)


def get_acl_groups_by_object(object, permission):
    """
    return list of users that have access from groups to object on "permission" permission
    """
    return ACLPermission.objects.get_acl_groups_by_object(object, permission)


def get_acl_users_by_object(object, permission):
    """
    return list of users that have access explicitly to object on "permission" permission
    """
    return ACLPermission.objects.get_acl_users_by_object(object, permission)


class ACLVerbPermission(models.Model):

    name = models.CharField(_('name'), max_length=255)
    content_type = models.ForeignKey(ContentType)
    codename = models.CharField(_('codename'), max_length=100)
    # objects = PermissionManager()

    class Meta:
        verbose_name = _('permission')
        verbose_name_plural = _('permissions')
        unique_together = (('content_type', 'codename'),)
        ordering = ('content_type__app_label',
                    'content_type__model', 'codename')
        db_table = settings.TABLE_PREFIX + "verbs_permissions"

    def __unicode__(self):
        return u"%s | %s" % (
            unicode(self.content_type.app_label),
            unicode(self.codename))


class ACLPermissionManager(models.Manager):

    cache_childs_groups = {}

    def copy_permissions(self, obj_source, perm_source, obj_target, perm_target):
        ct_src = ContentType.objects.get_for_model(obj_source)
        ct_target = ContentType.objects.get_for_model(obj_target)
        self.filter(object_id=obj_target.pk, content_type=ct_target,
                    permission__codename=perm_target).delete()
        perms = self.filter(
            object_id=obj_source.pk, content_type=ct_src, permission__codename=perm_source)
        check = get_acl_for_model(obj_target)
        for p in perms:
            if p.user:
                check.add_perm(perm_target, obj_target, p.user, p.acltype)
            else:
                check.add_perm(perm_target, obj_target, p.group, p.acltype)

    def cache_acl_groups(self, object):
        acl_groups_permissions = cache.get(ACL_GROUPS_PERMISSIONS_CACHE)
        if acl_groups_permissions is None:
            acl_groups_permissions = {}

        ct = ContentType.objects.get_for_model(object)
        acl_groups_permissions[ct.pk] = {}
        acl_groups_permissions[ct.pk][object.id] = {}

        perms = self.filter(group__isnull=False, object_id=object.pk, content_type=ct).values(
            'permission__codename', 'content_type', 'object_id', 'group', 'acltype').order_by('group__lft')
        for p in perms:
            try:
                groups = acl_groups_permissions[ct.pk][
                    object.id][p['permission__codename']]
            except:
                groups = acl_groups_permissions[ct.pk][
                    object.id][p['permission__codename']] = []

            if p['group'] > defines.ZORNA_GROUP_REGISTERED:
                if p['acltype'] == defines.ZORNA_PERMISSIONS_DENY_CHILDS or p['acltype'] == defines.ZORNA_PERMISSIONS_ALLOW_CHILDS:  # Deny++ and Allow++
                    if p['group'] not in self.cache_childs_groups:
                        self.cache_childs_groups[p['group']] = UserGroup.objects.get(
                            pk=p['group']).get_descendants(True)
                    childs = self.cache_childs_groups[p['group']]
                    childs_id = [g.pk for g in childs]
                    if p['acltype'] == defines.ZORNA_PERMISSIONS_DENY_CHILDS:
                        groups = [groups - childs_id for groups, childs_id in zip(
                            groups, childs_id)]
                    else:
                        groups.extend(childs_id)
                elif p['acltype'] == defines.ZORNA_PERMISSIONS_ALLOW:
                    groups.append(p['group'])
                else:
                    groups = [g for g in groups if g != p['group']]
            else:
                if p['acltype'] == defines.ZORNA_PERMISSIONS_ALLOW or p['acltype'] == defines.ZORNA_PERMISSIONS_ALLOW_CHILDS:
                    groups.append(p['group'])
                else:
                    groups = [g for g in groups if g != p['group']]

            acl_groups_permissions[ct.pk][object.id][
                p['permission__codename']] = groups

        cache.set(ACL_GROUPS_PERMISSIONS_CACHE, acl_groups_permissions)
        return acl_groups_permissions

    def get_acl_groups_by_object(self, object, permission):
        """
        return list of groups that have access to object on "permission" permission
        """
        ct = ContentType.objects.get_for_model(object)
        acl_groups_permissions = cache.get(ACL_GROUPS_PERMISSIONS_CACHE)
        if acl_groups_permissions is None:
            acl_groups_permissions = self.cache_acl_groups(object)
        else:
            try:
                acl_groups_permissions[ct.pk][object.pk]
            except KeyError:
                acl_groups_permissions = self.cache_acl_groups(object)
        try:
            return acl_groups_permissions[ct.pk][object.id][permission]
        except:
            return []

    def get_acl_by_object(self, object, permission):
        """
        return list of users that have access to object on "permission" permission ( explicitly or from groups )
        """
        allowed_groups = self.get_acl_groups_by_object(object, permission)
        if defines.ZORNA_GROUP_PUBLIC in allowed_groups or defines.ZORNA_GROUP_REGISTERED in allowed_groups:
            members = User.objects.filter(
                is_active=1).order_by('first_name', 'last_name')
        else:
            ct = ContentType.objects.get_for_model(object)
            members = User.objects.filter(Q(user_profile__groups__in=allowed_groups) | Q(
                aclpermission__permission__codename=permission, aclpermission__content_type=ct, aclpermission__object_id=object.pk)).distinct().order_by('first_name', 'last_name')
        return members

    def get_acl_users_by_object(self, object, permission):
        """
        return list of users that have access explicitly to object on "permission" permission
        """
        ct = ContentType.objects.get_for_model(object)
        members = User.objects.filter(
            Q(aclpermission__permission__codename=permission, aclpermission__content_type=ct,
              aclpermission__object_id=object.pk)).distinct().order_by('first_name', 'last_name')
        return members

    def get_acl_objects_by_model(self, user, model, permission):
        """
        return all objects of the specified model for which user "user" have permission "permission"
        """
        contenttype = ContentType.objects.get_for_model(model)

        ret = []
        user_id = 0 if user.is_anonymous() else user.id
        acl_users_permissions = cache.get(ACL_USERS_PERMISSIONS_CACHE)
        if acl_users_permissions is None:
            acl_users_permissions = self.cache_user_permissions(user)
        else:
            try:
                acl_users_permissions[user_id]
            except KeyError:
                acl_users_permissions = self.cache_user_permissions(user)
        try:
            for obj, perm in acl_users_permissions[user_id][contenttype.id].iteritems():
                try:
                    if perm[permission]:
                        ret.append(obj)
                except:
                    pass
        except:
            pass
        return ret

    def cache_user_permissions(self, user):
        acl_users_permissions = cache.get(ACL_USERS_PERMISSIONS_CACHE)
        if acl_users_permissions is None:
            acl_users_permissions = {}

        if user.is_anonymous():
            user_id = 0
            user_groups = []
        else:
            user_groups = [g.id for g in user.get_profile().groups.all()]
            user_id = user.id

        acl_users_permissions[user_id] = {}

        # p =
        # ACLPermission.objects.select_related().filter(group__isnull=False).values('permission__codename',
        # 'object__content_type', 'object', 'group', 'acltype')

        perms = self.filter(group__isnull=False).values(
            'permission__codename', 'content_type', 'object_id', 'group', 'acltype')
        for p in perms:
            if not acl_users_permissions[user_id].has_key(p['content_type']):
                acl_users_permissions[user_id][p['content_type']] = {}
            if not acl_users_permissions[user_id][p['content_type']].has_key(p['object_id']):
                acl_users_permissions[user_id][p[
                    'content_type']][p['object_id']] = {}

            bok = False
            if p['group'] == defines.ZORNA_GROUP_PUBLIC:  # Public
                bok = True
            elif p['group'] == defines.ZORNA_GROUP_ANONYMOUS and user.is_anonymous():  # Anonymous
                bok = True
            elif p['group'] == defines.ZORNA_GROUP_REGISTERED and user.is_anonymous() is False:
                bok = True
            elif p['group'] > defines.ZORNA_GROUP_REGISTERED:
                if p['acltype'] == defines.ZORNA_PERMISSIONS_DENY_CHILDS or p['acltype'] == defines.ZORNA_PERMISSIONS_ALLOW_CHILDS:  # Deny++ and Allow++
                    if p['group'] not in self.cache_childs_groups:
                        self.cache_childs_groups[p['group']] = UserGroup.objects.get(
                            pk=p['group']).get_descendants(True)
                    childs = self.cache_childs_groups[p['group']]
                    for c in childs:
                        if c.pk in user_groups:
                            bok = True
                            break
                else:
                    if p['group'] in user_groups:
                        bok = True

            if bok:
                acl_users_permissions[user_id][p['content_type']][p['object_id']][p['permission__codename']] = True if p[
                    'acltype'] == defines.ZORNA_PERMISSIONS_ALLOW_CHILDS or p['acltype'] == defines.ZORNA_PERMISSIONS_ALLOW else False
                acl_users_permissions[user_id][p['content_type']][
                    p['object_id']]['ct'] = p['content_type']

        if user_id:
            perms = self.filter(user=user).values(
                'permission__codename', 'content_type', 'object_id', 'group', 'acltype')
            for p in perms:
                if p['content_type'] not in acl_users_permissions[user_id]:
                    acl_users_permissions[user_id][p['content_type']] = {}
                if p['object_id'] not in acl_users_permissions[user_id][p['content_type']]:
                    acl_users_permissions[user_id][p[
                        'content_type']][p['object_id']] = {}

                acl_users_permissions[user_id][p['content_type']][p['object_id']][p[
                    'permission__codename']] = True if p['acltype'] == defines.ZORNA_PERMISSIONS_ALLOW else False
                acl_users_permissions[user_id][p['content_type']][
                    p['object_id']]['ct'] = p['content_type']

        cache.set(ACL_USERS_PERMISSIONS_CACHE, acl_users_permissions)
        return acl_users_permissions

    def user_permissions(self, user, obj, perm):
        """
                try:
                    return self.get(user=user, permission = perm, object=obj.id)
                except ACLPermission.DoesNotExist:
                    return False
        """
        user_id = 0 if user.is_anonymous() else user.id
        acl_users_permissions = cache.get(ACL_USERS_PERMISSIONS_CACHE)
        if acl_users_permissions is None:
            acl_users_permissions = self.cache_user_permissions(user)
        else:
            try:
                acl_users_permissions[user_id]
            except KeyError:
                acl_users_permissions = self.cache_user_permissions(user)

        try:
            ct = ContentType.objects.get_for_model(obj)
            return acl_users_permissions[user_id][ct.pk][obj.id][perm.codename]
        except:
            return False

    def group_permissions(self, group, obj, perm):
        try:
            return self.get(group=group, permission=perm, object=obj.id)
        except ACLPermission.DoesNotExist:
            return False

    def delete_user_permissions(self, permission, obj):
        ct = ContentType.objects.get_for_model(obj)
        ACLPermission.objects.filter(
            permission__codename=permission, object_id=obj.pk, content_type=ct, user__isnull=False).delete()

PERMISSIONS_TYPES = (
    (0, 'Deny'),
    (1, 'Deny+'),
    (2, 'Allow'),
    (3, 'Allow+'),
)


class ACLPermission(models.Model):

    permission = models.ForeignKey(ACLVerbPermission)
    object_id = models.IntegerField()
    content_type = models.ForeignKey(ContentType)
    user = models.ForeignKey(User, null=True)
    group = models.ForeignKey(UserGroup, null=True, related_name='acl_group')
    acltype = models.IntegerField(
        max_length=1, choices=PERMISSIONS_TYPES, default=0)
    content_object = generic.GenericForeignKey('content_type', 'object_id')
    objects = ACLPermissionManager()

    class Meta:
        verbose_name = _('Acl permission')
        verbose_name_plural = _('Acl permissions')
        unique_together = ((
            'permission', 'object_id', 'content_type', 'user', 'group'),)
        db_table = settings.TABLE_PREFIX + "permissions"

    def __unicode__(self):
        return u"%s" % (
            unicode(self.permission.codename))


class ACLGotInvalidModel(Exception):
    pass


class ACLException(Exception):
    pass


class NotAModel(ACLException):

    def __init__(self, object):
        super(NotAModel, self).__init__(
            "Not a model class or instance")


class UnsavedModelInstance(ACLException):

    def __init__(self, object):
        super(UnsavedModelInstance, self).__init__(
            "Model instance has no pk, was it saved?")


class ACLMetaclass(type):

    """
    Used to generate the default set of permission
    """
    def __new__(cls, name, bases, attrs):
        new_class = super(ACLMetaclass, cls).__new__(cls, name, bases, attrs)
        if new_class.model is None:
            return new_class
        new_class.perms = {}
        if not isinstance(new_class.model, (Model, ModelBase)):
            raise NotAModel(new_class.model)
        elif isinstance(new_class.model, Model) and not new_class.model.pk:
            raise UnsavedModelInstance(new_class.model)

        content_type = ContentType.objects.get_for_model(new_class.model)
        current_perms = content_type.model_class().get_acl_permissions()
        register_acl_for_model(new_class.model, current_perms)

        perms = ACLVerbPermission.objects.filter(content_type=content_type)
        for perm in perms:
            if current_perms.has_key(perm.codename) is False:
                ACLPermission.objects.filter(permission=perm).delete()
                perm.delete()
                continue

            new_class.perms[perm.codename] = perm
            func = cls.create_check(new_class, perm.codename)
            object_name = new_class.model._meta.object_name
            func_name = "%s_%s" % (perm.codename, object_name.lower())
            func.short_description = _("Can %(check)s this %(object_name)s") % {
                'object_name': new_class.model._meta.object_name.lower(),
                'check': perm.codename}
            func.k = perm.codename
            setattr(new_class, func_name, func)

        return new_class

    def create_check(self, check_name, *args, **kwargs):
        def check(self, *args, **kwargs):
            granted = self.can(check_name, *args, **kwargs)
            return granted
        return check


class BaseACL(object):

    model = None
    __metaclass__ = ACLMetaclass

    def __init__(self, *args, **kwargs):
        super(BaseACL, self).__init__(*args, **kwargs)

    def get_acl_groups_forms(self, request, object_id, **kwargs):
        extra_context = {}
        obj = self.model.objects.get(pk=object_id)
        ct = ContentType.objects.get_for_model(obj)

        extra_context["object_name"] = unicode(obj)

        try:
            exclude = kwargs['exclude']
            ob_list = UserGroup.objects.exclude(pk__in=exclude).order_by('lft')
        except:
            ob_list = UserGroup.objects.all().order_by('lft')
        template = 'acl/acl_groups.html'

        extra_context["proto_perm"] = None
        perm_object = obj
        if request.method == 'POST':
            if 'bupdate' not in request.POST:
                pr = request.POST.get("proto_perm", None)
                if pr:
                    perm_object = self.model.objects.get(pk=pr)
                    extra_context["proto_perm"] = perm_object.pk
            else:
                # obj.aclpermission_set.filter(group__isnull=False).delete()
                ACLPermission.objects.filter(
                    object_id=obj.pk, content_type=ct, group__isnull=False).delete()
                temp = {}
                for ac in request.POST:
                    if ac[:5] == 'perm_' and request.POST[ac] != "":
                        acc = ac.split('_')
                        try:
                            temp[int(acc[2])].append((
                                acc[1], request.POST[ac]))
                        except KeyError:
                            temp[int(acc[2])] = []
                            temp[int(acc[2])].append((
                                acc[1], request.POST[ac]))

                if len(temp):
                    grp = UserGroup.objects.in_bulk(temp.keys())
                    for g_id, g in grp.iteritems():
                        for c in temp[g_id]:
                            self.add_perm(c[0], obj, g, c[1])
                perm_object = obj
                # invalidate cache
                cache.delete(ACL_USERS_PERMISSIONS_CACHE)
                cache.delete(ACL_GROUPS_PERMISSIONS_CACHE)
                redirect = request.POST.get('next', None)
                if redirect:
                    if '?' not in redirect:
                        redirect = redirect + '?object_id=' + str(object_id)
                    else:
                        redirect = redirect + '&object_id=' + str(object_id)
                    return HttpResponseRedirect(redirect)

        extra_context["verbs"] = {}
        for k, v in self.perms.iteritems():
            extra_context["verbs"][k] = {}
            extra_context["verbs"][k]['text'] = _(v.name)

        # perm =
        # perm_object.aclpermission_set.select_related().filter(group__isnull=False)
        perm = ACLPermission.objects.select_related().filter(
            object_id=perm_object.pk, content_type=ct, group__isnull=False)
        parentsid = []
        for ob in ob_list:
            if ob.pk > defines.ZORNA_GROUP_REGISTERED:
                ob.show_members = True
            else:
                ob.show_members = False
            ob.verbs = {}
            for k, v in self.perms.iteritems():
                ob.verbs[k] = ''
                for g in perm:
                    if g.group_id == ob.id and k == g.permission.codename:
                        ob.verbs[k] = g.acltype
            if ob.parent_id not in parentsid:
                ob.parent_id = 0
            parentsid.append(ob.pk)
        # e =
        # ZornaEntity.objects.filter(aclpermission__object__content_type=ContentType.objects.get_for_model(obj)).distinct()
        extra_context['perm_objects'] = {}
        # for c in e:
        # c = c.as_leaf_class() #TODO A optimiser
        #    extra_context['perm_objects'].append(c)

        perm = ACLPermission.objects.select_related().filter(
            content_type=ct, group__isnull=False).exclude(object_id=obj.pk)
        for p in perm:
            try:
                extra_context['perm_objects'][
                    p.content_object.pk] = p.content_object
            except:
                pass

        extra_context['next'] = request.REQUEST.get('next', '')
        extra_context['object'] = obj
        extra_context['object_list'] = ob_list
        context = RequestContext(request)
        return render_to_response(template, extra_context, context_instance=context)

    def get_acl_users_forms(self, request, object_id, template='acl/user_acl_users.html'):
        extra_context = {}
        obj = self.model.objects.get(pk=object_id)
        ct = ContentType.objects.get_for_model(obj)
        extra_context["object_name"] = unicode(obj)

        acl_template = 'acl/acl_users.html'

        if request.method == 'POST':
            selected = request.POST.getlist('_selected_action')
            selected_verbs = request.POST.getlist('_selected_verbs')
            # delete permission
            # obj.aclpermission_set.filter(object=obj,
            # user__isnull=False).delete()
            ACLPermission.objects.filter(
                object_id=obj.pk, content_type=ct, user__isnull=False).delete()

            # add permission for cchecked users
            ol = User.objects.filter(pk__in=selected)
            for u in ol:
                for ac in request.POST:
                    if ac[:5] == 'perm_' and request.POST[ac] != "":
                        acc = ac.split('_')
                        if acc[2] in selected and long(acc[2]) == u.pk:
                            self.add_perm(acc[1], obj, u, request.POST[ac])

            # add permission for new user
            u = request.POST.get("u", "")
            if u:
                u = User.objects.get(pk=u)
                check = get_acl_for_model(obj)
                for v in selected_verbs:
                    check.add_perm(v, obj, u, defines.ZORNA_PERMISSIONS_ALLOW)
            # invalidate cache
            cache.delete(ACL_USERS_PERMISSIONS_CACHE)

            redirect = request.POST.get('next', None)
            if redirect:
                if '?' not in redirect:
                    redirect = redirect + '?object_id=' + str(object_id)
                else:
                    redirect = redirect + '&object_id=' + str(object_id)
                return HttpResponseRedirect(redirect)

        extra_context["verbs"] = {}
        for k, v in self.perms.iteritems():
            extra_context["verbs"][k] = {}
            extra_context["verbs"][k]['text'] = _(v.name)

        ob_list = User.objects.filter(
            aclpermission__user__isnull=False, aclpermission__object_id=obj.pk, aclpermission__content_type=ct).distinct()
        # perm =  obj.aclpermission_set.select_related().filter(object=obj,
        # user__isnull=False)
        perm = ACLPermission.objects.select_related().filter(
            object_id=obj.pk, content_type=ct, user__isnull=False)
        dummy = ob_list
        for ob in dummy:
            ob.verbs = {}
            for k, v in self.perms.iteritems():
                ob.verbs[k] = ''
                for g in perm:
                    if g.user_id == ob.id and k == g.permission.codename:
                        ob.verbs[k] = g.acltype

        extra_context['users_list'] = dummy
        extra_context['object'] = obj
        extra_context['next'] = request.REQUEST.get('next', '')

        paginate_by = 20
        paginator = Paginator(
            ob_list._clone(), paginate_by, allow_empty_first_page=True)
        page = request.GET.get('page', 1)
        try:
            page_number = int(page)
        except ValueError:
            if page == 'last':
                page_number = paginator.num_pages
            else:
                # Page is not 'last', nor can it be converted to an int.
                raise Http404
        try:
            page_obj = paginator.page(page_number)
        except InvalidPage:
            raise Http404

        extra_context['paginator'] = paginator
        extra_context['page_obj'] = page_obj
        extra_context['acl_form'] = render_to_string(
            acl_template, extra_context, context_instance=RequestContext(request))
        context = RequestContext(request)
        if template:
            return render_to_response(template, extra_context, context_instance=context)
        else:
            return extra_context

    def has_user_perms(self, perm, obj, user):
        if user.is_superuser:
            return True
        # check if a Permission object exists for the given params
        return ACLPermission.objects.user_permissions(user, obj, perm)

    def has_group_perms(self, perm, obj, group):
        perms = ACLPermission.objects.group_permissions(group, obj, perm)
        return perms

    def can(self, check, obj, user_or_group, *args, **kwargs):

        if isinstance(user_or_group, User) or isinstance(user_or_group, AnonymousUser):
            return self.has_user_perms(self.perms[check], obj, user_or_group)
        if isinstance(user_or_group, UserGroup):
            return self.has_group_perms(self.perms[check], obj, user_or_group)

    def add_perm(self, check, obj, user_or_group, acltype):

        kwargs = {}

        if isinstance(user_or_group, User):
            kwargs['user'] = user_or_group
        elif isinstance(user_or_group, UserGroup):
            kwargs['group'] = user_or_group
        else:
            return None

        content_type = ContentType.objects.get_for_model(obj)
        kwargs['permission'] = self.perms[check]
        kwargs['object_id'] = obj.pk
        kwargs['content_type'] = content_type
        kwargs['acltype'] = acltype

        acl = ACLPermission.objects.get_or_create(**kwargs)
        cache.delete(ACL_USERS_PERMISSIONS_CACHE)
        cache.delete(ACL_GROUPS_PERMISSIONS_CACHE)
        return acl

    def remove_perm(self, check, obj, user_or_group, acltype):

        kwargs = {}

        if isinstance(user_or_group, User):
            kwargs['user'] = user_or_group
        elif isinstance(user_or_group, UserGroup):
            kwargs['group'] = user_or_group
        else:
            return False
        content_type = ContentType.objects.get_for_model(obj)
        ACLPermission.objects.filter(
            object_id=obj.pk, content_type=content_type, user=user_or_group, permission=self.perms[check]).delete()
        cache.delete(ACL_USERS_PERMISSIONS_CACHE)
        cache.delete(ACL_GROUPS_PERMISSIONS_CACHE)
        return True
