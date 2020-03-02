package LeetCode;

import java.util.Comparator;
import java.util.PriorityQueue;

public class _023_MergekSortedLists {
    /**
     * 注意边界情况：
     * case 1: [[1,2,3]] // 只有一个链表
     * case 2: [[], []] // 有多个链表，但都是空链表
     * case 3: [[], [1,2,3]] // 有多个链表，同时包含空链表和非空链表
     *
     * 时间复杂度：O()
     */
    /*
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 0) return null;
        if (lists.length == 1) return lists[0];

        ListNode dummyHead = new ListNode(-1);
        ListNode pre = dummyHead;

        int[] isEnd = new int[lists.length];
        int nullNodeCount = 0;

        while (nullNodeCount < lists.length - 1) {
            ListNode minNode = null;
            int minIdx = 0;
            int minVal = Integer.MAX_VALUE;

            for (int i = 0; i < lists.length; i++) {
                if (isEnd[i] == 0 && lists[i] == null) {
                    nullNodeCount++;
                    isEnd[i] = 1;
                }
                if (lists[i] != null && lists[i].val <= minVal) {
                    minVal = lists[i].val;
                    minNode = lists[i];
                    minIdx = i;
                }
            }
            if(pre != null) {
                pre.next = minNode;
                pre = pre.next;
            }

            if(lists[minIdx] != null)
                lists[minIdx] = lists[minIdx].next;
        }

        return dummyHead.next;
    }
    */

    /**
     * 每次遍历求最小值可以使用优先队列来进行优化
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if(lists.length == 0) return null;
        if(lists.length == 1) return lists[0];

        // 优先队列，较小值排在前面
        PriorityQueue<ListNode> queue = new PriorityQueue<>(lists.length, (o1, o2) -> {
            if(o2.val < o1.val) return 1;
            else if(o2.val > o1.val) return -1;
            else return 0;
        });
        // 初始化优先队列
        for(ListNode node : lists){
            if(node != null) queue.add(node);
        }

        ListNode dummyHead = new ListNode(-1);
        ListNode pre = dummyHead;
        while (!queue.isEmpty()) {
            pre.next = queue.poll();
            pre = pre.next;

            if(pre.next != null) queue.add(pre.next);
        }
        return dummyHead.next;
    }

    public static void main(String[] args) {
        _023_MergekSortedLists obj = new _023_MergekSortedLists();
        // normal case
        ListNode l1 = ListNode.makeList(new int[]{1, 2, 2, 5, 6});
        ListNode l2 = ListNode.makeList(new int[]{2, 3, 4});
        ListNode l3 = ListNode.makeList(new int[]{0, 9});
        ListNode l4 = ListNode.makeList(new int[]{7, 8});
        ListNode[] lists = new ListNode[]{l1, l2, l3, l4};
        ListNode head = obj.mergeKLists(lists);
        ListNode.showList(head);
        System.out.println();

        // case 1
        ListNode l5 = ListNode.makeList(new int[]{1, 2, 5, 6});
        ListNode[] lists2 = new ListNode[]{l5};
        head = obj.mergeKLists(lists2);
        ListNode.showList(head);
        System.out.println();

        // case 2
        ListNode l6 = null, l7 = null;
        ListNode[] lists3 = new ListNode[]{l6, l7};
        head = obj.mergeKLists(lists3);
        ListNode.showList(head);
        System.out.println();

        // case 3
        ListNode l8 = ListNode.makeList(new int[]{1});
        ListNode l9 = null;
        ListNode[] lists4 = new ListNode[]{l8,l9};
        head = obj.mergeKLists(lists4);
        ListNode.showList(head);
    }
}
