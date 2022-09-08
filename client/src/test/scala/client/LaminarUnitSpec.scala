package client

import com.raquo.domtestutils.matching.*
import com.raquo.domtestutils.scalatest.{Matchers, MountSpec}
import com.raquo.domtestutils.{MountOps, Utils}
import com.raquo.laminar.api.L
import com.raquo.laminar.defs.ReactiveComplexHtmlKeys.{CompositeHtmlAttr, CompositeProp}
import com.raquo.laminar.defs.ReactiveComplexSvgKeys.CompositeSvgAttr
import com.raquo.laminar.nodes.{ReactiveElement, RootNode}
import org.scalatest.funspec.AnyFunSpec

/** These utils are needed to test Laminar reactive elements with `domtestutils`, and are
  * derived from `LaminarSpec`. Source:
  * https://github.com/raquo/Laminar/blob/da47b0233dc579efce393447109d5aab82c08360/src/test/scala/com/raquo/laminar/utils/LaminarSpec.scala
  */

trait LaminarUnitSpec
    extends AnyFunSpec
    with Utils
    with MountOps
    with MountSpec
    with Matchers
    with RuleImplicits:

  // why it's `null`: https://github.com/raquo/Laminar/blob/da47b0233dc579efce393447109d5aab82c08360/src/test/scala/com/raquo/laminar/utils/LaminarSpec.scala#L15-L19
  private var root: RootNode = null

  def mount(
      node: ReactiveElement.Base,
      clue: String = defaultMountedElementClue
  ): Unit = {
    mountedElementClue = clue
    assertEmptyContainer("laminar.mount")
    root = L.render(containerNode, node)
  }

  override def unmount(clue: String = "unmount"): Unit = {
    assertRootNodeMounted("unmount:" + clue)
    doAssert(
      root != null,
      s"ASSERT FAILED [unmount:$clue]: Laminar root not found. " +
        "Did you use Laminar's mount() method in LaminarSpec?"
    )
    doAssert(
      root.child.ref == rootNode,
      s"ASSERT FAILED [unmount:$clue]: Laminar root's ref does not match rootNode."
    )
    doAssert(
      root.unmount(),
      s"ASSERT FAILED [unmount:$clue]: Laminar root failed to unmount"
    )
    root = null
    // containerNode = null
    mountedElementClue = defaultMountedElementClue
  }

  def expectChildren(childRules: Rule*): Unit =
    val rules: Seq[Rule] = ExpectedNode.comment +: childRules
    expectNode(L.div.of(rules*))

  given [V]: Conversion[CompositeProp[V], TestableProp[V, V]] = prop =>
    new TestableProp(prop.key)

  given [V]: Conversion[CompositeHtmlAttr[V], TestableHtmlAttr[V]] = attr =>
    new TestableHtmlAttr(attr.key)

  given [V]: Conversion[CompositeSvgAttr[V], TestableSvgAttr[V]] = attr =>
    new TestableSvgAttr(attr.key)
