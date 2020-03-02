### 栈问题

题目列表：

1. [20. 有效的括号](https://leetcode-cn.com/problems/valid-parentheses/)
2. [71. 简化路径](https://leetcode-cn.com/problems/simplify-path/)
3. [224. 基本计算器](https://leetcode-cn.com/problems/basic-calculator/) 
4. [227. 基本计算器 II](https://leetcode-cn.com/problems/basic-calculator-ii/) 
5. [772. 基本计算器 III](https://leetcode-cn.com/problems/basic-calculator-iii/) （没做）
6. [150. 逆波兰表达式求值](https://leetcode-cn.com/problems/evaluate-reverse-polish-notation/)
7. [155. 最小栈](https://leetcode-cn.com/problems/min-stack/)
8. [225. 用队列实现栈](https://leetcode-cn.com/problems/implement-stack-using-queues/)
9. [232. 用栈实现队列](https://leetcode-cn.com/problems/implement-queue-using-stacks/)



##### [20. 有效的括号](https://leetcode-cn.com/problems/valid-parentheses/) [四星]

给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。有效字符串需满足：

* 左括号必须用相同类型的右括号闭合。
* 左括号必须以正确的顺序闭合。

注意空字符串可被认为是有效字符串。

分析：括号匹配问题是栈最经典的应用场景。下面的代码写的非常trick。

```java
public boolean isValid(String s) {
    if (s.isEmpty())
        return true;
    if (s.length() % 2 == 1) // 长度为奇数时必不可能成对，因此直接返回false
        return false;

    Stack<Character> stack = new Stack<>();
    for (char c : s.toCharArray()) {
        if (c == '(')
            stack.push(')');
        else if (c == '{')
            stack.push('}');
        else if (c == '[')
            stack.push(']');
        else if (stack.empty() || c != stack.pop())
            return false;
    }
    return stack.empty();
}
```



##### [71. 简化路径](https://leetcode-cn.com/problems/simplify-path/) [四星]

分析：本题给出一个不规范/不正确的 Unix 风格的绝对路径，要求转化成一个正确规范的绝对路径。一开始看到这个题目的时候根本无从下手，字符串处理的问题，感觉处理起来会非常琐碎。受评论区大神启发，对测试用例分析了一下，还是很快解出了。先来看看测试用例：

```
由于给定的路径中每一级的目录之间都由一个或多个‘/’隔开，所以想到对path通过‘/’来分隔。即，String[] s = path.split("/");
path = "/a//b////c/d//././/.."
s    = [][a][][b][][][][c][d][][.][.][][..]

path = "/a/../../b/../c//.//"
s    = [][a][..][..][b][..][c][][.]

path = "/home//foo/"
s    = [][home][][foo]
```

可以看到，分隔后的String数组由4类元素组成，分别是`空字符`， `.` ，`..` ，`目录名` 。根据规范，我们只需要关心`..`和`目录名` 即可。考虑到这里，关键就来了！这里使用栈来处理，对于每一个`目录名`，我们将其压入栈中，而当遇到`..`时，就把栈顶元素弹出，以表示回到上一层目录。特别的，如果遇到`..`时栈已经为空了，则表示已经回到了根目录`/`。最后，只要把栈中的元素自底向上拼接起来即可（栈底的目录表示上一层的目录）。

```java
public String simplifyPath(String path) {
    Stack<String> stack = new Stack<>();
    String[] s = path.split("/");
    
    for (String value : s) {
        if (value.equals("") || value.equals(".")) {
            continue;
        }
        if (value.equals("..")) {
            if (!stack.empty()) {
                stack.pop();
            }
        } else {
            stack.push("/" + value);
        }
    }

    if(stack.empty()){
        return "/";
    }

    StringBuilder result = new StringBuilder();
    while (!stack.empty()){
        result.insert(0, stack.pop());
    }
    return result.toString();
}
```



##### [224. 基本计算器](https://leetcode-cn.com/problems/basic-calculator/) [五星]

实现一个基本的计算器来计算一个简单的字符串表达式的值。字符串表达式可以包含左括号 `(` ，右括号 `)`，加号 `+` ，减号 `-`，**非负**整数和空格 。比如，输入: "(1+(4+5+2)-3)+(6+8)"，输出: 23。**可以假定所有给定的表达式都是有效的**。

分析：核心思想是**将一个数和在这个数之前的符号构成一个操作数operand**，这样整个表达式就只有加法这一种运算了。需借助栈来完成，参考[这里](https://leetcode-cn.com/problems/basic-calculator/solution/ji-ben-ji-suan-qi-by-leetcode/) 。特别的，关于第一个数值得处理可能会觉得比较麻烦，因为第一个数值如果是正数，前面并不会出现符号。其实大可不必担心，对于任意一个表达式，都可以认为其前面**加上**了一个“**正0**”，初始化符号sign=1，操作数operand=0，即可统一处理。这种思想有点像链表处理问题中，每次操作都需要一个pre节点，但是对于链表的首个节点而言，它并不存在pre节点，于是我们就创建一个dummy节点置于首个节点之前，便于统一处理。

基本算法如下：正向遍历字符串

* 如果遇到数字，通过`operand = operand * 10 + (c-'0')` 进行转化，因为一个操作数可能由多个数字组成，比如123可以由100 + 20 + 3 构成，这种方法在第7题整数反转中有过应用，也是基本常识。
* 每当遇到‘+’或‘-’运算符时，总是把该符号**之前的**数值加入到最终结果中，并把该符号作为**下一个数值**（或由括号括起来的一个整体）计算时用的符号！
* 如果遇到左括号 `(`，我们将迄今为止计算的结果和符号push到栈上，然后重新开始进行计算，就像计算一个新的表达式一样。
* 如果遇到右括号 `)`，则首先把括号内的表达式计算完整，再乘上括号`()` 前的符号，并把“括号表达式的结果”与之前存放在栈中的“括号表达式之前的结果”进行相加，然后进入下一轮迭代计算。

代码：我感觉这代码是非常优美~需要多写几遍好好体会！

```java
// 时间复杂度：O(n) 一趟遍历，所有元素仅访问一遍
// 空间复杂度：O(n)
public int calculate(String s) {
    Stack<Integer> stack = new Stack<>();
    int result = 0, operand = 0, sign = 1;//sign=1表示＋，sign=-1表示-

    for(char c : s.toCharArray()){
        if(Character.isDigit(c)){
            operand = operand*10 + c - '0';
        }else if(c == '-'){
            result += sign * operand;
            operand = 0;
            sign = -1; // 这是下一个数字前的符号
        }else if(c == '+'){
            result += sign * operand;
            operand = 0;
            sign = 1; // 这是下一个数字前的符号
        }else if(c == '('){
            // 如果遇到左括号，则先把之前的计算结果放入栈中保存
            stack.push(result);
            stack.push(sign);
            // 括号内的表达式作为一个整体，因此重置result, operand和sign
            result = 0;
            operand = 0;
            sign = 1;
        }else if(c == ')'){
            // 首先把括号内的表达式计算完
            result += sign * operand;
            // 此时result表示括号内表达式的值，但是还要加上括号前的符号
            result *= stack.pop();
            // 再加上之前求出的结果
            result += stack.pop();
            operand = 0;
            sign = 1;
        }
    }
    return result + (sign * operand);
}
```



##### [227. 基本计算器 II](https://leetcode-cn.com/problems/basic-calculator-ii/) [五星]

实现一个基本的计算器来计算一个简单的字符串表达式的值。字符串表达式仅包含非负整数，`+`， `-` ，`*`，`/` 四种运算符和空格 。 整数除法仅保留整数部分。可以假设所给定的表达式都是有效的。

分析：由于存在运算优先级，我们采取的措施是使用一个栈保存数字，如果该数字**之前的**符号是加或减，那么把当前数字压入栈中，注意如果是减号，则加入当前数字的相反数，因为减去一个数相当于加上一个相反数。如果**之前的**符号是乘或除，那么从栈顶取出一个数字和当前数字进行乘或除的运算，再把结果压入栈中，那么完成一趟遍历后，所有的乘或除都运算完了，再把栈中所有的数字都加起来就是最终结果了。

这里需要注意的是在遍历过程中不要把“当前的符号”和“之前的符号”混淆起来，由于第一个数字之前可能不存在符号，因此初始化符号op为'+'，作为第一个数字之前的符号。示意图如下：

![leetcode227](D:\workspace\algorithm\img\leetcode227.png)

代码：

```java
public int calculate(String s) {
    Stack<Integer> stack = new Stack<>();
    int result = 0, operand = 0;
    char op = '+';

    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);

        if (Character.isDigit(c)) {
            operand = operand * 10 + (c - '0');
        }
        // 注意这里不是else if，因此走完上一个if判断仍然会进入这个if判断
        // 这么处理是为了方便处理表达式的最后一个数值
        // c 等于+,-,*,/，或已经到达最后一个位置
        if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) { 
            // 注意这里的 op 表示的是当前操作数operand之前的符号，而不是当前s[i]所指的符号，请结合示意图
            if (op == '+') stack.push(operand);
            else if (op == '-') stack.push(-operand);
            else if (op == '*' || op == '/') {
                // 更严谨的话这里应该要防止溢出，但貌似不处理也能通过测试，但凡两个int相乘就留个心眼
                int temp = op == '*' ? stack.pop() * operand : stack.pop() / operand;
                stack.push(temp);
            }
            op = c;
            operand = 0;
        }
    }

    while (!stack.isEmpty()) {
        result += stack.pop();
    }

    return result;
}
```



##### [772. 基本计算器 III](https://leetcode-cn.com/problems/basic-calculator-iii/) [五星]

本题为付费题，不过可以参考[这里](https://www.cnblogs.com/grandyang/p/8873471.html)。本题结合了前面两道题，不仅包含`+`， `-` ，`*`，`/` 四种运算符和空格，还同时含有括号`()`。比如，$2*(5+5*2)/3+(6/2+8)= 21$，$(2+6* 3+5- (3*14/7+2)*5)+3 = -12$ 。

分析：

代码：

```

```



##### [150. 逆波兰表达式求值](https://leetcode-cn.com/problems/evaluate-reverse-polish-notation/) [二星，什么是后缀表达式]

根据逆波兰表示法，求表达式的值。有效的运算符包括 +, -, *, / 。每个运算对象可以是整数，也可以是另一个逆波兰表达式。说明：

* 整数除法只保留整数部分。
* 给定逆波兰表达式**总是有效**的。换句话说，表达式总会得出有效数值且不存在除数为 0 的情况。

```
输入: ["10", "6", "9", "3", "+", "-11", "", "/", "", "17", "+", "5", "+"]
输出: 22
解释: 
  ((10 * (6 / ((9 + 3) * -11))) + 17) + 5
= ((10 * (6 / (12 * -11))) + 17) + 5
= ((10 * (6 / -132)) + 17) + 5
= ((10 * 0) + 17) + 5
= (0 + 17) + 5
= 17 + 5
= 22
```

分析：本题的难点主要是对逆波兰表示法的概念已经忘了。其实是非常简单的。在数据结构教材中关于栈的应用一节中有讲表达式求值，我们称`(4 + (13 / 5))` 这样的为中缀表达式，称`["4", "13", "5", "/", "+"]` 这样的为后缀表达式（也就是逆波兰表示法）。通过将我们日常的中缀表达式转化成后缀表达式（这里也是出题点），再通过后缀表达式求值，就可以化繁为简。本题考察的就是如何对后缀表达式进行求值。其计算过程是：顺序遍历后缀表达式，

* 如果遇到操作数，则压入栈中；
* 如果遇到操作符op，则依次弹出栈顶元素a, b，并将 `b <op> a` 的结果压入栈中。 当最后栈中只剩一个元素时就是结果。

```java
public int evalRPN(String[] tokens) {
    Stack<Integer> stack = new Stack<>();
    for(String s : tokens){
        if(s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/")) {
            int a = stack.pop();
            int b = stack.pop();
            switch (s){
                case "+":
                    stack.push(b + a);
                    break;
                case "-":
                    stack.push(b - a);
                    break;
                case "*":
                    stack.push(b * a);
                    break;
                case "/":
                    stack.push(b / a);
                    break;
            }
        }else {
            stack.push(Integer.parseInt(s));
        }
    }
    return stack.peek();
}
```



##### [155. 最小栈](https://leetcode-cn.com/problems/min-stack/) [五星]

设计一个支持 push，pop，top 操作，并能在**常数时间**内检索到最小元素的栈。

push(x) -- 将元素 x 推入栈中。
pop() -- 删除栈顶的元素。
top() -- 获取栈顶元素。
getMin() -- 检索栈中的最小元素。

```
示例:
MinStack minStack = new MinStack();
minStack.push(-2);
minStack.push(0);
minStack.push(-3);
minStack.getMin();   --> 返回 -3.
minStack.pop();
minStack.top();      --> 返回 0.
minStack.getMin();   --> 返回 -2.
```

分析：用两个栈来实现，一个数据栈`data`正常存放数据，另一个辅助栈`helper`的栈顶元素则维护着当前数据栈`data`中的最小元素。

由于是用Java实现的，多说一句。Java的集合中存放的是对象`Integer`而不是基本数据类型`int` ，`int` 和 `Integer` 是可以直接比较的，这是因为auto boxing/unboxing 机制。但是要特别注意的是，不要用两个 `Integer` 对象来直接进行数值大小的比较，因为这样比较的是对象的地址！这一点在编码时要注意。在《Effective Java》中有这一条目的详细说明。

```java
// 时间复杂度：O(1)
// 空间复杂度：O(n)
class MinStack {
    private Stack<Integer> data; // 存放数据
    private Stack<Integer> helper; // helper的栈顶元素始终为当前data栈中元素的最小值
    /** initialize your data structure here. */
    public MinStack() {
        data = new Stack<>();
        helper = new Stack<>();
    }

    public void push(int x) {
        data.add(x);
        // 新元素较小，或者新元素小于当前helper的栈顶元素时，插入
        if(helper.isEmpty() || x <= helper.peek()){
            helper.add(x);
        }
    }

    public void pop() {
        if (!data.isEmpty()) {
            int peek = data.peek();
            data.pop();
            if (peek == helper.peek()) { // 这里int与Integer可直接进行比较（自动装箱/拆箱机制）
                helper.pop();
            }
        } else {
            throw new RuntimeException("ERROR: stack is empty.");
        }
    }

    public int top() {
        if(!data.isEmpty()){
            return data.peek();
        }else {
            throw new RuntimeException("ERROR: stack is empty.");
        }
    }

    public int getMin() {
        if(!helper.isEmpty()) {
            return helper.peek();
        }else {
            throw new RuntimeException("ERROR: stack is empty.");
        }
    }
}
```



##### [225. 用队列实现栈](https://leetcode-cn.com/problems/implement-stack-using-queues/) [四星，第一次碰见]

使用队列实现栈的下列操作：

push(x) -- 元素 x 入栈
pop() -- 移除栈顶元素
top() -- 获取栈顶元素
empty() -- 返回栈是否为空

注意:

* 你只能使用队列的基本操作-- 也就是 push to back, peek/pop from front, size, 和 is empty 这些操作是合法的。
* 你可以假设所有操作都是有效的（例如, 对一个空的栈不会调用 pop 或者 top 操作）。

分析：第一个想法是，用两个队列来模拟栈，由于队列是先进先出的，因此，每次插入元素时，都要把队列中原有的元素先暂存到另外一个辅助队列中，从而把新插入的元素放在队首，然后再从辅助队列中把元素转移到数据队列中。因此，每次插入数据时的开销比较大O(n)，而取队首元素则是O(1)。

看到一个写法是只用一个队列就可以了，而不需要借助辅助栈。每次插入数据时，先是正常插入，这个时候新元素在队尾；然后再把队列前`size-1`个元素逐个出队，又逐个入队，即`queue.add(queue.poll())`，从而可以转换元素的位置了。

```java
// 时间复杂度：插入元素O(n), 取出元素O(1)
// 空间复杂度：O(n)
class MyStack {
    /** Initialize your data structure here. */
    private Queue<Integer> data;
    public MyStack() {
        data = new LinkedList<>();
    }
    /** Push element x onto stack. */  // 还是挺妙的~
    public void push(int x) {
        data.add(x);
        for(int i=0; i<data.size()-1;i++){
            data.add(data.poll());
        }
    }
    /** Removes the element on top of the stack and returns that element. */
    public int pop() {
        return data.poll();
    }
    /** Get the top element. */
    public int top() {
        return data.peek();
    }
    /** Returns whether the stack is empty. */
    public boolean empty() {
        return data.isEmpty();
    }
}
```



##### [232. 用栈实现队列](https://leetcode-cn.com/problems/implement-queue-using-stacks/) [三星，时间复杂度的分析]

使用栈实现队列的下列操作：

push(x) -- 将一个元素放入队列的尾部。
pop() -- 从队列首部移除元素。
peek() -- 返回队列首部的元素。
empty() -- 返回队列是否为空。

说明:

* 你只能使用标准的栈操作 -- 也就是只有 push to top, peek/pop from top, size, 和 is empty 操作是合法的。
* 假设所有操作都是有效的 （例如，一个空的队列不会调用 pop 或者 peek 操作）。

分析：用两个栈实现队列（分别记为`s1`和`s2`），push元素时，就往s1中push；取元素则从s2中取。每次取元素时，先判断s2是否为空，如果不为空，则直接取走s2的栈顶元素即可；如果为空，则把s1中的全部元素push到s2中。若s1也为空，则队列为空。

看了题解发现自己的这个思路是属于比较高效的一种，下面是[官方题解](https://leetcode-cn.com/problems/implement-queue-using-stacks/solution/yong-zhan-shi-xian-dui-lie-by-leetcode/)提供的实现，就以它的为主了。

```java
// 时间复杂度：摊还复杂度 O(1)，最坏情况下的时间复杂度 O(n)
// 空间复杂度: O(n)
class MyQueue {
    private Stack<Integer> s1;
    private Stack<Integer> s2;
    private int front;
    public MyQueue() {
        s1 = new Stack<>();
        s2 = new Stack<>();
    }

    // 时间复杂度:O(1); 
    // 空间复杂度:O(n)
    public void push(int x) {
        if(s1.isEmpty()) {
            front = x;
        }
        s1.push(x);
    }

    // 时间复杂度：摊还复杂度 O(1)，最坏情况下的时间复杂度 O(n)
    // 空间复杂度：O(1)
    public int pop() {
        if(s2.isEmpty()){
            while (!s1.empty()) {
                s2.push(s1.pop());
            }
        }
        return s2.pop();
    }
    
    // 时间复杂度：O(1)，要么是s2的栈顶元素，要么就是在push时就已经记录好了的
    // 空间复杂度：O(1)
    // 但是这种写法只适用于本题中“限定不会对空队列进行peek/pop操作”，否则在队列为空时还会返回front，是错的
    public int peek() {
        if(!s2.empty()) {
            return s2.peek();
        }
        return front;
    }
  	/* 安全的写法
     public int peek() {
        if(!s2.isEmpty()){
            return s2.peek();
        }else if(!s1.isEmpty()){
            while (!s1.isEmpty()){
                s2.push(s1.pop());
            }
            return s2.peek();
        }else {
            throw new RuntimeException("ERROR: queue is empty");
        }
    }*/
    public boolean empty() {
        return s1.isEmpty() && s2.isEmpty();
    }
}
```

关于`pop()`操作的时间复杂度分析如下（参考官方题解的），这一题的核心是理解“**摊还分析**”的概念。

在最坏情况下，s2 为空，算法需要从 s1 中弹出 n 个元素，然后再把这 n 个元素压入 s2，在这里n代表队列的大小。这个过程产生了 **2n** 步操作，时间复杂度为 O(n)。但当 s2 非空时，算法就只有 O(1) 的时间复杂度。所以为什么叫做摊还复杂度 O(1) 呢？

**摊还分析给出了所有操作的平均性能**。摊还分析的核心在于，**最坏情况下的操作一旦发生了一次，那么在未来很长一段时间都不会再次发生，这样就会均摊每次操作的代价**。

来看下面这个例子，从一个空队列开始，依次执行下面这些操作：

$$push_1, push_2, \ldots, push_n, pop_1, pop_2 \ldots, pop_n $$


单次 出队 操作最坏情况下的时间复杂度为 O(n)。考虑到我们要做 n次出队操作，如果我们用最坏情况下的时间复杂度来计算的话，那么所有操作的时间复杂度为 $O(n^2)$。然而，在一系列的操作中，**最坏情况不可能每次都发生**，可能一些操作代价很小，另一些代价很高。因此，如果用传统的最坏情况分析，那么给出的时间复杂度是远远大于实际的复杂度的。例如，在一个动态数组里面只有一些插入操作需要花费线性的时间，而其余的一些插入操作只需花费常量的时间。

在上面的例子中，**出队 操作最多可以执行的次数跟它之前执行过 入队 操作的次数有关**。虽然一次 出队 操作代价可能很大，但是每 n 次 入队 才能产生这么一次代价为 n 的 出队 操作。因此所有操作的总时间复杂度为：n(所有的入队操作产生） + 2 * n(第一次出队操作产生） + n - 1(剩下的出队操作产生）， 所以实际时间复杂度为 O(2*n)。于是我们可以得到每次操作的**平均时间复杂度**为 O(2n/2n)=O(1)。


