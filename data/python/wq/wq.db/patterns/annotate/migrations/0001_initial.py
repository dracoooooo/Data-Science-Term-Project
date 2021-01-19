# -*- coding: utf-8 -*-
# Generated by Django 1.9.1 on 2016-01-12 03:18
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('contenttypes', '0002_remove_content_type_name'),
    ]

    operations = [
        migrations.CreateModel(
            name='Annotation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('value', models.TextField(blank=True, null=True)),
                ('object_id', models.PositiveIntegerField(db_index=True)),
                ('content_type', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='contenttypes.ContentType')),
            ],
            options={
                'abstract': False,
                'db_table': 'wq_annotation',
            },
        ),
        migrations.CreateModel(
            name='AnnotationType',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
            ],
            options={
                'abstract': False,
                'db_table': 'wq_annotationtype',
            },
        ),
        migrations.AlterUniqueTogether(
            name='annotationtype',
            unique_together=set([('name',)]),
        ),
        migrations.AddField(
            model_name='annotation',
            name='type',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='annotate.AnnotationType'),
        ),
    ]