package client

import com.raquo.laminar.api.L.*

class NavBarSpec extends LaminarUnitSpec:

  val expectedNavBar =
    div.of(
      nav.of(
        div.of(
          a.of(
            img.of(
              src is "http://localhost/static/svg/bug-fill.svg",
              widthAttr is 30,
              heightAttr is 24
            ),
            b.of("Threat Monitor")
          ),
          div.of(
            label.of(
              b.of("Threats table")
            ),
            input.of(
              tpe is "checkbox",
              defaultChecked is false
            )
          )
        )
      )
    )

  it("should render the navbar with default checkbox state") {

    mount(NavBar.navbar)

    expectNode(expectedNavBar)
  }
