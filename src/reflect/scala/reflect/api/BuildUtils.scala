package scala
package reflect
package api

/**
 * This is an internal implementation class.
 * @groupname TreeBuilders Tree Building
 */
private[reflect] trait BuildUtils { self: Universe =>

  /** @group TreeBuilders */
  val build: BuildApi

  // this API abstracts away the functionality necessary for reification
  // it's too gimmicky and unstructured to be exposed directly in the universe
  // but we need it in a publicly available place for reification to work

  /** @group TreeBuilders */
  abstract class BuildApi {
    /** Selects type symbol with given simple name `name` from the defined members of `owner`.
     */
    def selectType(owner: Symbol, name: String): TypeSymbol

    /** Selects term symbol with given name and type from the defined members of prefix type
     */
    def selectTerm(owner: Symbol, name: String): TermSymbol

    /** Selects overloaded method symbol with given name and index
     */
    def selectOverloadedMethod(owner: Symbol, name: String, index: Int): MethodSymbol

    /** A fresh symbol with given name `name`, position `pos` and flags `flags` that has
     *  the current symbol as its owner.
     */
    def newNestedSymbol(owner: Symbol, name: Name, pos: Position, flags: FlagSet, isClass: Boolean): Symbol

    /** Create a fresh free term symbol.
     *  @param   name   the name of the free variable
     *  @param   value  the value of the free variable at runtime
     *  @param   flags  (optional) flags of the free variable
     *  @param   origin debug information that tells where this symbol comes from
     */
    def newFreeTerm(name: String, value: => Any, flags: FlagSet = NoFlags, origin: String = null): FreeTermSymbol

    /** Create a fresh free type symbol.
     *  @param   name   the name of the free variable
     *  @param   flags  (optional) flags of the free variable
     *  @param   origin debug information that tells where this symbol comes from
     */
    def newFreeType(name: String, flags: FlagSet = NoFlags, origin: String = null): FreeTypeSymbol

    /** Set symbol's type signature to given type.
     *  @return the symbol itself
     */
    def setTypeSignature[S <: Symbol](sym: S, tpe: Type): S

    /** Set symbol's annotations to given annotations `annots`.
     */
    def setAnnotations[S <: Symbol](sym: S, annots: List[Annotation]): S

    def flagsFromBits(bits: Long): FlagSet

    def This(sym: Symbol): Tree

    def Select(qualifier: Tree, sym: Symbol): Select

    def Ident(sym: Symbol): Ident

    def Block(stats: List[Tree]): Block

    def TypeTree(tp: Type): TypeTree

    def thisPrefix(sym: Symbol): Type

    def setType[T <: Tree](tree: T, tpe: Type): T

    def setSymbol[T <: Tree](tree: T, sym: Symbol): T

    def mkAnnotationCtor(tree: Tree, args: List[Tree]): Tree

    val FlagsAsBits: FlagsAsBitsExtractor

    trait FlagsAsBitsExtractor {
      def unapply(flags: Long): Option[Long]
    }

    val TypeApplied: TypeAppliedExtractor

    trait TypeAppliedExtractor {
      def unapply(tree: Tree): Some[(Tree, List[Tree])]
    }

    val Applied: AppliedExtractor

    trait AppliedExtractor {
      def unapply(tree: Tree): Some[(Tree, List[List[Tree]])]
    }

    val SyntacticClassDef: SyntacticClassDefExtractor

    trait SyntacticClassDefExtractor {
      def apply(mods: Modifiers, name: TypeName, tparams: List[TypeDef],
                constrMods: Modifiers, vparamss: List[List[ValDef]], parents: List[Tree],
                selfdef: ValDef, body: List[Tree]): Tree
      def unapply(tree: Tree): Option[(Modifiers, TypeName, List[TypeDef], Modifiers,
                                       List[List[ValDef]], List[Tree], ValDef, List[Tree])]
    }

    val TupleN: TupleNExtractor
    val TupleTypeN: TupleNExtractor

    trait TupleNExtractor {
      def apply(args: List[Tree]): Tree
      def unapply(tree: Tree): Option[List[Tree]]
    }

    def RefTree(qual: Tree, sym: Symbol): Tree
  }
}
