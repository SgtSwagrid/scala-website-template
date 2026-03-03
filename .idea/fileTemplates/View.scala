#if ((${PACKAGE_NAME} && ${PACKAGE_NAME} != ""))package ${PACKAGE_NAME} #end

import com.raquo.laminar.api.L.*
import org.scalajs.dom.{document, window}
import scala.scalajs.js.annotation.JSExportTopLevel
import swagco.fairmap.views.View

@JSExportTopLevel("${VIEW_NAME}View")
class ${VIEW_NAME}View extends View:
  
  def content = div()
