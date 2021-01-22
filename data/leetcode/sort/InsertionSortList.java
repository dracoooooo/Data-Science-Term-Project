public class InsertionSortList {
    public ListNode insertionSortList(ListNode head) {
        if(head == null) return head;
        ListNode current = head.next;
        ListNode pre = head;
        while(current !=null){
            if(current.val>=pre.val){
                current = current.next;
                pre = pre.next;
            }
            else{
                pre.next = current.next;
                if(current.val<=head.val){ 
                    current.next = head;
                    head = current;
                }
                else{
                    ListNode search = head;
                    while(search.next != null && search.next.val<current.val){
                        search = search.next;
                    }
                    ListNode tmp = search.next;
                    search.next = current;
                    current.next = tmp;
                }
                current = pre.next;
            }
        }
        return head;
    }
}
