package cn.sun.miniredis.internal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class SkipList<T extends Comparable<T>> {
    private static final int MAX_LEVEL = 32;
    private static final double P = 0.25;
    private SkipListNode<T> head;
    private int level;
    private int size;
    private Random random;
    private SkipListNode<T> tail;

    @Getter
    public static class SkipListNode<T> {
        double score;
        T member;
        SkipListNode<T> backward;
        SkipListLevel[] levels;

        public SkipListNode(int l, double score, T member) {
            this.levels = new SkipListLevel[l];
            for (int i = 0; i < l; i++) {
                this.levels[i] = new SkipListLevel(null, 0);
            }
            this.score = score;
            this.member = member;
            this.backward = null;
        }

        static class SkipListLevel {
            SkipListNode forward;
            long span;

            public SkipListLevel(SkipListNode forward, long span) {
                this.forward = forward;
                this.span = span;
            }
        }
    }

    public SkipList() {
        head = new SkipListNode<>(MAX_LEVEL, Double.NEGATIVE_INFINITY, null);
        level = 1;
        size = 0;
        this.random = new Random();
    }

    public SkipListNode<T> insert(double score, T member) {
        // 创建update数组，用以处处该插入的位置
        SkipListNode<T>[] update = new SkipListNode[MAX_LEVEL];
        long[] rank = new long[MAX_LEVEL];
        SkipListNode<T> cur_node = head;
        
        // 从最高层开始遍历，找到每一层新节点应该插入的位置，并且记录每一层的跨度
        for (int i = level - 1; i >= 0; i--) {
            rank[i] = (i == level - 1 ? 0 : rank[i + 1]);
            while (cur_node.levels[i].forward != null
                    && (cur_node.levels[i].forward.score < score ||
                        (cur_node.levels[i].forward.score == score && compare((T)cur_node.levels[i].forward.member, member) < 0 ))) {
                rank[i] += cur_node.levels[i].span;
                cur_node = cur_node.levels[i].forward;
            }
            update[i] = cur_node;
        }

        // 算出新的level
        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) {
                rank[i] = 0;
                update[i] = head;
                update[i].levels[i].span = size;
            }
            level = newLevel;
        }

        // 创建新节点
        cur_node = new SkipListNode<>(newLevel, score, member);
        // 将新节点插入到跳表每一层
        for (int i = 0; i < newLevel; i++) {
            cur_node.levels[i].forward = update[i].levels[i].forward;
            update[i].levels[i].forward = cur_node;
            cur_node.levels[i].span = update[i].levels[i].span - (rank[0] - rank[i]);
            update[i].levels[i].span = (rank[0] - rank[i]) + 1;
        }
        // 更新每一层的跨度
        for (int i = newLevel; i < level; i++) update[i].levels[i].span++;

        // 更新新节点的backward(前驱节点)
        cur_node.backward = (update[0] == head) ? null : update[0];

        // 更新新节点的forward(后继节点)
        if (cur_node.levels[0].forward != null) {
            cur_node.levels[0].forward.backward = cur_node;
        } else {
            tail = cur_node;
        }
        // 更新size
        size ++;
        return cur_node;
    }

    public boolean delete(double score, T member) {
        SkipListNode<T>[] update = new SkipListNode[MAX_LEVEL];
        SkipListNode<T> cur_node = head;

        for (int i = level - 1; i >= 0; i--) {
            cur_node = getPosition(score, member, cur_node, i, update);
        }
        // 获取要删除的节点
        cur_node = cur_node.levels[0].forward;
        // 判断节点是否为空且满足条件
        if (cur_node != null && cur_node.score == score && compare((T)cur_node.member, member) == 0) {
            skipListDelete(cur_node, update);
            return true;
        }
        return false;
    }

    // 获取指定范围内的节点
    public List<SkipListNode<T>> getElementByRankRange(int start, int end) {
        List<SkipListNode<T>> resultList = new ArrayList<>();

        // 检查边界条件，不符合则返回空list
        if (size == 0 || start > end || start >= size || end < 0) {
            return resultList;
        }

        // 处理边界条件
        start = Math.max(0, start);
        end = Math.min(end, size - 1);
        // redis的下标从0开始，而跳表下标从1开始
        start += 1;
        end += 1;

        SkipListNode<T> cur_node = head;
        long travesed = 0;

        // 从最高层开始遍历，快速找到该节点应该在的位置
        for (int i = level - 1; i >= 0; i--) {
            while (cur_node.levels[i].forward != null && travesed + cur_node.levels[i].span < start) {
                travesed += cur_node.levels[i].span;
                cur_node = cur_node.levels[i].forward;
            }
        }

        // 从该节点开始，遍历到指定位置
        travesed ++;
        cur_node = cur_node.levels[0].forward;
        // 在第0层遍历，将所有范围内的节点加入list
        while (cur_node != null && travesed <= end) {
            resultList.add(cur_node);
            travesed ++;
            cur_node = cur_node.levels[0].forward;
        }
        return resultList;
    }

    // 获取指定排名的节点
    public SkipListNode<T> getElementByRank(long rank) {
        // 检查边界条件，不符合则返回null
        if (rank <= 0 || rank > size) {
            return null;
        }

        SkipListNode<T> cur_node = head;
        long travesed = 0;
        for (int i = level - 1; i >= 0; i--) {
            while (cur_node.levels[i].forward != null && travesed + cur_node.levels[i].span <= rank) {
                travesed += cur_node.levels[i].span;
                cur_node = cur_node.levels[i].forward;
            }
            if (travesed == rank) {
                return cur_node;
            }
        }
        return null;
    }

    // 获取指定分数范围的节点
    public List<SkipListNode<T>> getElementByScoreRange(double min_score, double max_score) {
        List<SkipListNode<T>> resultList = new ArrayList<>();
        SkipListNode<T> cur_node = head;

        for (int i = level - 1; i >= 0; i--) {
            while (cur_node.levels[i].forward != null && cur_node.levels[i].forward.score < min_score) {
                cur_node = cur_node.levels[i].forward;
            }
        }

        // 获取在 min_score 和 max_score 范围内的score最小的节点
        cur_node = cur_node.levels[0].forward;
        // 在第0层遍历，将所有范围内的节点加入list
        while (cur_node != null && cur_node.score <= max_score) {
            resultList.add(cur_node);
            cur_node = cur_node.levels[0].forward;
        }
        return resultList;
    }

    // 删除节点
    private void skipListDelete(SkipListNode<T> curNode, SkipListNode<T>[] update) {
        // 遍历，删除该节点在每一层的记录，并更新每一层删除节点前驱节点的指向的后继节点 和 span
        for(int i = 0; i < level; i++) {
            if (update[i].levels[i].forward == curNode) {
                update[i].levels[i].forward = curNode.levels[i].forward;
                update[i].levels[i].span += curNode.levels[i].span - 1;
            } else {
                update[i].levels[i].span --;
            }
        }

        // 删除节点的后继节点存在
        if (curNode.levels[0].forward != null) {
            curNode.levels[0].forward.backward = curNode.backward;
        } else {
            //  删除节点即尾节点，则将tail指向该节点的前驱节点
            tail = curNode.backward;
        }

        //  更新level，删除空层
        while (level > 1 && head.levels[level - 1].forward == null)
            level --;

        // 更新size
        size --;
    }

    /**
     * 用途：查找当前节点应该在的位置
     * 1. 节点后驱节点不能为空
     * 2. 节点后驱节点的分数比当前节点小
     * 3. 如果节点后驱节点的分数等于当前节点，后驱节点的member小于当前节点
     * 4. 以上3种情况均不满足，则插入
     */
    private SkipListNode<T> getPosition(double score, T member, SkipListNode<T> cur_node, int i, SkipListNode<T>[] update) {
        while (cur_node.levels[i].forward != null
                && (cur_node.levels[i].forward.score < score ||
                    (cur_node.levels[i].forward.score == score && compare((T)cur_node.levels[i].forward.member, member) < 0))) {
            cur_node = cur_node.levels[i].forward;
        }
        update[i] = cur_node;
        return cur_node;
    }

    // 随机生成level
    private int randomLevel() {
        int level = 1;
        while (random.nextDouble() < P && level < MAX_LEVEL) {
            level++;
        }
        return level;
    }

    private int compare(T o1, T o2) {
        if (o1 == null || o2 == null) {
            throw new NullPointerException("can't be null");
        }
        return o1.compareTo(o2);
    }
}
