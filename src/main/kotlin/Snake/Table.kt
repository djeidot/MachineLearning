package Snake

import java.io.File

class Table {
    val contents = mutableListOf<String>()

    fun add(vararg items: Float) {
        contents.add(items.joinToString(",") { it.toString() })
    }
    
    fun add(vararg items: String) {
        contents.add(items.joinToString(","))
    }

    fun save(filename: String) {
        val file = File(filename)
        file.createNewFile()
        file.printWriter().use {
            for (line in contents) {
                it.println(line)
            }
        }
    }


    fun setAt(index: Int, vararg items: String) {
        if (index <= contents.lastIndex) {
            contents.removeAt(index)
        }
        contents.add(index, items.joinToString(","))
    }
    
    fun getAt(index: Int) = contents[index].split(",")
    
    fun isEmpty() = contents.isEmpty()

    companion object {
        fun load(filename: String): Table {
            val t = Table()
            t.contents.clear()
            val file = File(filename)
            if (file.exists()) {
                t.contents.addAll(File(filename).readLines())
            }
            return t
        }
    }
}