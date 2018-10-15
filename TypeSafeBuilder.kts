import kotlin.reflect.*

class HTMLStyle {

    private val propertyMap = mutableMapOf<String, String?>()
    var width: String? by Delegate()
    var height: String? by Delegate()
    var font_size: String? by Delegate()

    //when accesing property, use backing map
    inner class Delegate {
        operator fun getValue(thisRef: HTMLStyle, property: KProperty<*>): String? {
            return propertyMap.get(property.name)
        }

        operator fun setValue(thisRef: HTMLStyle, property: KProperty<*>, value: String?) {
            propertyMap.put(property.name, value)
        }
    }

    //rename property names to style names
    fun getStyles(): MutableMap<String, String?> {
        val retStyles = mutableMapOf<String, String?>()
        for ((prop_name, value) in propertyMap.iterator()) {
            when (prop_name) {
                "font_size" -> retStyles.put("font-size", value)
                else -> retStyles.put(prop_name, value)
            }
        }
        return retStyles
    }
}

interface Element {
    fun print(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun print(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

abstract class Tag(val name: String) : Element {

    val children = arrayListOf<Element>()
    val style = HTMLStyle()

    override fun print(builder: StringBuilder, indent: String) {
        builder.append("$indent<$name${printStyles()}>\n")
        for (c in children) {
            c.print(builder,  "$indent  ")
        }
        builder.append("$indent</$name>\n")
    }

    private fun printStyles(): String {
        val builder = StringBuilder()
        val styles = style.getStyles()
        if (!styles.isEmpty()) {
            builder.append(" style=\"")
            for ((styleName, value) in styles) {
                builder.append("$styleName:$value;")
            }
            builder.append("\"")
        }
        return builder.toString()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        print(builder, "")
        return builder.toString()
    }

}


class Span : Tag("span") {
    operator fun plus(other: String) {
        children.add(TextElement(other))
    }
}

class Div : Tag("div") {
    fun span(init: Span.() -> Unit) : Span {
        val span = Span()
        span.init()
        children.add(span)
        return span
    }
}

//initial function
fun div(init: Div.() -> Unit): Div {
    val divElement = Div()
    divElement.init()
    return divElement
}

fun result() =
        div {
            span {
                this+"Hello"
                this.style.width = "100%"
                this.style.font_size = "56"
            }
            span {
                this+"World"
                this.style.width = "100%"
                this.style.font_size = "65"
            }
        }

print(result())