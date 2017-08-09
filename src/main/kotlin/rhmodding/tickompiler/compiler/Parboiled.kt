package rhmodding.tickompiler.compiler

import org.parboiled.Action
import org.parboiled.BaseParser
import org.parboiled.Rule
import org.parboiled.annotations.BuildParseTree
import org.parboiled.annotations.SuppressNode
import org.parboiled.annotations.SuppressSubnodes
import org.parboiled.support.Position
import org.parboiled.support.StringVar
import org.parboiled.support.Var
import org.parboiled.trees.ImmutableBinaryTreeNode
import org.parboiled.trees.ImmutableTreeNode
import org.parboiled.trees.MutableTreeNodeImpl
import org.parboiled.trees.TreeNode
import rhmodding.tickompiler.CompilerError

abstract class StatementNode<T : TreeNode<T>>(val position: Position) : ImmutableTreeNode<T>()

class FunctionCallNode(position: Position, val func: String, val special: ExpressionNode?,
                       val args: List<ExpressionNode>) : StatementNode<FunctionCallNode>(position) {

    override fun toString(): String {
        return "[$func $special $args]"
    }

}

class VarAssignNode(position: Position, val variable: String, val expr: ExpressionNode) : StatementNode<VarAssignNode>(position) {

    override fun toString(): String {
        return "$variable = $expr"
    }

}

class AliasAssignNode(position: Position, val alias: String, val expr: ExpressionNode) : StatementNode<AliasAssignNode>(position) {

    override fun toString(): String {
        return "#alias $alias $expr"
    }

}

class MarkerNode(position: Position, val name: String) : StatementNode<MarkerNode>(position) {
    override fun toString(): String {
        return "$name:"
    }
}

class DirectiveNode(position: Position, val name: String, val num: Long) : StatementNode<DirectiveNode>(position) {
    override fun toString(): String {
        return "#$name 0x${num.toString(16).toUpperCase()}"
    }
}

class LoopNode(position: Position, val statements: List<ImmutableTreeNode<*>>, val expr: ExpressionNode) : StatementNode<LoopNode>(position) {
    override fun toString(): String {
        var str = "Loop $expr times {\n"
        statements.forEach {
            str += it.toString().lines().map { "\t" + it }.joinToString("\n") + "\n"
        }
        str += "}"
        return str
    }
}

class StatementsNode(val position: Position, val list: MutableList<StatementNode<*>> = mutableListOf()) : MutableTreeNodeImpl<StatementsNode>()

class ArgsNode(val position: Position, val list: MutableList<ExpressionNode> = mutableListOf()) : MutableTreeNodeImpl<ArgsNode>()

class ExpressionNode constructor(val position: Position, rawop: String, left: ExpressionNode?,
                                 right: ExpressionNode?) : ImmutableBinaryTreeNode<ExpressionNode>(left, right) {
    val op = rawop.replace("[ \\t\\n]".toRegex(), "")
    var id: String? = null
    var string: String? = null
    var num: Long? = null

    constructor(position: Position, str: String, variable: Boolean = true) : this(position, "", null, null) {
        if (variable) {
            this.id = str
        } else {
            this.string = str
        }
    }

    constructor(position: Position, num: Long) : this(position, "", null, null) {
        this.num = num
    }

    fun getValue(variables: Map<String, Pair<Long, Compiler.VariableType>>): Long {
        if (num != null) {
            return num as Long
        }
        if (id != null) {
            return variables[id as String]?.first ?: throw CompilerError("Variable $id not initialized")
        }
        if (string != null) {
            return variables[string as String]?.first ?: throw CompilerError("String $string not properly handled. This should never happen.")
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
        return Sequence(VariableIdentifier(), Ch(':'), push(MarkerNode(position(), pop() as String)))
    }

    open fun Directive(): Rule {
        return Sequence(Ch('#'), VariableIdentifier(),
                        Optional(Whitespace()), IntegerLiteral(),
                        push(DirectiveNode(position(), pop(1) as String,
                                           (pop() as ExpressionNode).getValue(
                                                   emptyMap())))
                       )
    }

    open fun AliasAssignment(): Rule {
        return Sequence("#alias", Optional(Whitespace()), VariableIdentifier(), Optional(Whitespace()), Expression(),
                        push(AliasAssignNode(position(), pop(1) as String,
                                             pop() as ExpressionNode)))
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
                Sequence(FirstOf(HexInteger(), BinaryInteger(), DecimalInteger()), push(
                        ExpressionNode(position(), pop() as Long))),
                Sequence('-', FirstOf(HexInteger(), BinaryInteger(), DecimalInteger()),
                         push(ExpressionNode(position(), -(pop() as Long)))))
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
                push(ExpressionNode(position(), pop() as String))
                       )
    }

    open fun StringContents(): Rule {
        val name = StringVar()
        return Sequence(
                ZeroOrMore(NoneOf("\\\"")),
                name.append(match()),
                ZeroOrMore("\\", FirstOf("\"", "\\"), ZeroOrMore(NoneOf("\\\""))),
                Action<Any> {name.append(match().replace("\\\\(.)".toRegex(), {it.groupValues[1]}))},
                push(name.get())
                )
    }

    open fun DoubleQuoteString(): Rule {
        return Sequence('"', StringContents(), '"',
                        push(ExpressionNode(position(), pop() as String, false)))
    }

    open fun VariableAssignment(): Rule {
        return Sequence(VariableIdentifier(),
                        Optional(Whitespace()).suppressNode(),
                        Ch('='),
                        Optional(Whitespace()).suppressNode(),
                        Expression(),
                        push(VarAssignNode(position(), pop(1) as String,
                                           pop() as ExpressionNode)))
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
        return Sequence(push(ArgsNode(position())),
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
                        push(FunctionCallNode(position(), name.get(), special.get(),
                                              pop() as List<ExpressionNode>))
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
                                        push(ExpressionNode(position(), op.get(),
                                                            pop(1) as ExpressionNode,
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
        return FirstOf(IntegerLiteral(), VariableReference(), DoubleQuoteString(), Sequence(
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
                push(StatementsNode(position())),
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
                push(LoopNode(position(), pop() as List<ImmutableTreeNode<*>>,
                              pop() as ExpressionNode))
                       )
    }

}
