#if ((${PACKAGE_NAME} && ${PACKAGE_NAME} != ""))package ${PACKAGE_NAME} #end

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom.{document, window}

class ${COMPONENT_NAME}Component:
  
  def content: ReactiveElement.Base = div()
