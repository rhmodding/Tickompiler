package chrislo27.tickompiler.compiler

import chrislo27.tickompiler.CompilerError
import org.parboiled.Action
import org.parboiled.BaseParser
import org.parboiled.Rule
import org.parboiled.annotations.BuildParseTree
import org.parboiled.annotations.SuppressNode
import org.parboiled.annotations.SuppressSubnodes
import org.parboiled.support.StringVar
import org.parboiled.support.Var
import org.parboiled.trees.ImmutableBinaryTreeNode
import org.parboiled.trees.ImmutableTreeNode
import org.parboiled.trees.MutableTreeNodeImpl
import org.parboiled.trees.TreeNode

abstract class StatementNode<T : TreeNode<T>> : ImmutableTreeNode<T>()

class FunctionCallNode(val func: String, val special: ExpressionNode?,
                       val args: List<ExpressionNode>) : StatementNode<FunctionCallNode>() {

    override fun toString(): String {
        return "[$func $special $args]"
    }

}

class VarAssignNode(val variable: String, val expr: ExpressionNode) : StatementNode<VarAssignNode>() {

    override fun toString(): String {
        return "$variable = $expr"
    }

}

class AliasAssignNode(val alias: String, val expr: ExpressionNode) : StatementNode<AliasAssignNode>() {

    override fun toString(): String {
        return "#alias $alias $expr"
    }

}

class MarkerNode(val name: String) : StatementNode<MarkerNode>() {
    override fun toString(): String {
        return "$name:"
    }
}

class DirectiveNode(val name: String, val num: Long) : StatementNode<DirectiveNode>() {
    override fun toString(): String {
        return "#$name 0x${num.toString(16).toUpperCase()}"
    }
}

class LoopNode(val statements: List<ImmutableTreeNode<*>>, val expr: ExpressionNode) : StatementNode<LoopNode>() {
    override fun toString(): String {
        var str = "Loop $expr times {\n"
        statements.forEach {
            str += it.toString().lines().map { "\t" + it }.joinToString("\n") + "\n"
        }
        str += "}"
        return str
    }
}

class StatementsNode(val list: MutableList<StatementNode<*>> = mutableListOf()) : MutableTreeNodeImpl<StatementsNode>()

class ArgsNode(val list: MutableList<ExpressionNode> = mutableListOf()) : MutableTreeNodeImpl<ArgsNode>()

class ExpressionNode constructor(rawop: String, left: ExpressionNode?,
                                 right: ExpressionNode?) : ImmutableBinaryTreeNode<ExpressionNode>(left, right) {
    val op = rawop.replace("[ \\t\\n]".toRegex(), "")
    var id: String? = null
    var num: Long? = null

    constructor(id: String) : this("", null, null) {
        this.id = id
    }

    constructor(num: Long) : this("", null, null) {
        this.num = num
    }

    fun getValue(variables: Map<String, Long>): Long {
        if (num != null) {
            return num as Long
        }
        if (id != null) {
            return variables[id as String] ?: throw CompilerError("Variable $id not initialized")
        }
        return when (op) {
            "+" -> left().getValue(variables) + right().getValue(variables)
            "-" -> left().getValue(variables) - right().getValue(variables)
            "*" -> left().getValue(variables) * right().getValue(variables)
            "/" -> {
                val rightVal = right().getValue(variables)
                if (rightVal == 0L)
                    throw CompilerError("Division by 0")
                left().getValue(variables) / right().getValue(variables)
            }
            "<<" -> left().getValue(variables) shl right().getValue(variables).toInt()
            ">>" -> left().getValue(variables) ushr right().getValue(variables).toInt()
            "|" -> left().getValue(variables) or right().getValue(variables)
            "^" -> left().getValue(variables) xor right().getValue(variables)
            "&" -> left().getValue(variables) and right().getValue(variables)
            else -> throw CompilerError("This should never happen, please contact devs :(")
        }
    }
}

// TODO fix generics
@BuildParseTree
open class TickflowParser : BaseParser<Any>() {

    open fun TickflowCode(): Rule {
        return Sequence(OneOrMore(Statement()), EOI)
    }

    open fun Statement(): Rule {
        return Sequence(Optional(Whitespace()),
                        Optional(Sequence(FirstOf(
                                Directive(),
                                Marker(),
                                VariableAssignment(),
                                AliasAssignment(),
                                FunctionCall()
                                                 ), Optional(Whitespace()))),
                        AnyOf(";\n"))
    }

    open fun Marker(): Rule {
        return Sequence(VariableIdentifier(), Ch(':'), push(MarkerNode(pop() as String)))
    }

    open fun Directive(): Rule {
        return Sequence(Ch('#'), VariableIdentifier(),
                        Optional(Whitespace()), IntegerLiteral(),
                        push(DirectiveNode(pop(1) as String, (pop() as ExpressionNode).getValue(emptyMap())))
                       )
    }

    open fun AliasAssignment(): Rule {
        return Sequence("#alias", Optional(Whitespace()), VariableIdentifier(), Optional(Whitespace()), Expression(),
                        push(AliasAssignNode(pop(1) as String, pop() as ExpressionNode)))
    }

    @SuppressNode
    open fun Whitespace(): Rule {
        return OneOrMore(AnyOf(" \t"))
    }

    @SuppressSubnodes
    open fun DecimalInteger(): Rule {
        return Sequence(OneOrMore(CharRange('0', '9')),
                        push(Integer.parseUnsignedInt(match(), 10).toLong())
                       )
    }

    @SuppressSubnodes
    open fun HexInteger(): Rule {
        return Sequence(String("0x"),
                        OneOrMore(AnyOf("0123456789abcdefABCDEF")),
                        push(Integer.parseUnsignedInt(match(), 16).toLong())
                       )
    }

    @SuppressSubnodes
    open fun BinaryInteger(): Rule {
        return Sequence(String("0b"),
                        OneOrMore(AnyOf("01")),
                        push(Integer.parseUnsignedInt(match(), 2).toLong())
                       )
    }

    @SuppressSubnodes
    open fun IntegerLiteral(): Rule {
        return FirstOf(
                Sequence(FirstOf(HexInteger(), BinaryInteger(), DecimalInteger()), push(ExpressionNode(pop() as Long))),
                Sequence('-', FirstOf(HexInteger(), BinaryInteger(), DecimalInteger()),
                         push(ExpressionNode(-(pop() as Long)))))
    }

    @SuppressSubnodes
    open fun SpecialArgument(expr: Var<ExpressionNode?>): Rule {
        return Sequence(Ch('<'),
                        Expression(),
                        Ch('>'),
                        expr.set(pop() as ExpressionNode))
    }

    @SuppressSubnodes
    open fun VariableIdentifier(): Rule {
        val name = StringVar()
        return Sequence(
                AnyOf("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$"),
                name.append(match()),
                ZeroOrMore(AnyOf("1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$")),
                name.append(match()),
                push(name.get()))
    }

    open fun VariableReference(): Rule {
        return Sequence(
                VariableIdentifier(),
                push(ExpressionNode(pop() as String))
                       )
    }

    open fun VariableAssignment(): Rule {
        return Sequence(VariableIdentifier(),
                        Optional(Whitespace()).suppressNode(),
                        Ch('='),
                        Optional(Whitespace()).suppressNode(),
                        Expression(),
                        push(VarAssignNode(pop(1) as String, pop() as ExpressionNode)))
    }

    @SuppressSubnodes
    open fun FunctionIdentifier(name: StringVar): Rule {
        return Sequence(FirstOf(IntegerLiteral(), VariableIdentifier()),
                        name.append(match()))
    }

    open fun Argument(): Rule {
        return Sequence(
                Expression(),
                (peek(1) as ArgsNode).list.add(pop() as ExpressionNode)
                       )
    }

    open fun FunctionArgs(): Rule {
        return Sequence(push(ArgsNode()),
                        Argument(),
                        ZeroOrMore(
                                Sequence(
                                        Optional(Whitespace()),
                                        Ch(',').suppressNode(),
                                        Optional(Whitespace()),
                                        Argument()
                                        )
                                  ),
                        push((pop() as ArgsNode).list.toList()))
    }

    open fun FunctionCall(): Rule {
        val name = StringVar("")
        val special = Var<ExpressionNode?>()

        return Sequence(FunctionIdentifier(name),
                        Optional(SpecialArgument(special)),
                        push(listOf<ExpressionNode>()),
                        Optional(// more args
                                FirstOf(
                                        Sequence(OneOrMore(Whitespace()).suppressNode(), FunctionArgs()),
                                        Sequence(
                                                Optional(Whitespace()).suppressNode(),
                                                Ch('(').suppressNode(),
                                                Optional(Whitespace()).suppressNode(),
                                                FunctionArgs(),
                                                Ch(')').suppressNode()
                                                )
                                       ),
                                Action<Any> { pop(1); true }
                                ),
                        push(FunctionCallNode(name.get(), special.get(), pop() as List<ExpressionNode>))
                       )
    }

    open fun OperatorRule(subRule: Rule, operatorRule: Rule): Rule {
        val op = Var<String>()
        return Sequence(subRule,
                        ZeroOrMore(
                                Sequence(
                                        Sequence(Optional(Whitespace()).suppressNode(), operatorRule,
                                                 Optional(Whitespace()).suppressNode()),
                                        op.set(match()),
                                        subRule,
                                        push(ExpressionNode(op.get(), pop(1) as ExpressionNode,
                                                            pop() as ExpressionNode))
                                        )
                                  )
                       )
    }

    open fun BitwiseOp(): Rule {
        return FirstOf("&", "|", "^", "<<", ">>")
    }

    open fun AddOp(): Rule {
        return FirstOf("+", "-")
    }

    open fun MultOp(): Rule {
        return FirstOf("*", "/")
    }

    open fun Expression(): Rule {
        return OperatorRule(BitwiseTerm(), BitwiseOp())
    }

    open fun BitwiseTerm(): Rule {
        return OperatorRule(AddTerm(), AddOp())
    }

    open fun AddTerm(): Rule {
        return OperatorRule(Factor(), MultOp())
    }

    open fun Factor(): Rule {
        return FirstOf(IntegerLiteral(), VariableReference(), Sequence(
                Ch('('),
                Expression(),
                Ch(')')
                                                                      )
                      )
    }

    open fun ListAppendStatement(): Rule {
        return Sequence(Statement(), (peek(1) as StatementsNode).list.add(pop() as StatementNode<*>))
    }

    open fun Statements(): Rule {
        return Sequence(
                push(StatementsNode()),
                OneOrMore(ListAppendStatement()),
                push((pop() as StatementsNode).list)
                       )
    }

    open fun Loop(): Rule {
        return Sequence(
                Expression(),
                Optional(Whitespace()).suppressNode(),
                "times",
                Optional(Whitespace()).suppressNode(),
                Ch('{'),
                Optional(Whitespace()).suppressNode(),
                Optional(Ch('\n')),
                Statements(),
                Ch('}'),
                push(LoopNode(pop() as List<ImmutableTreeNode<*>>, pop() as ExpressionNode))
                       )
    }

}
