import java.io.File
import java.io.IOException
import java.text.NumberFormat
import java.util.Arrays
import java.util.Comparator


import server.QueryHandler
import java.io.OutputStream
import java.io.PrintWriter

class DirectoryQuery(
		private val baseURL: String, 
		private val baseDir: File, 
		private val dir: File,
		rawOut: OutputStream, 
		out: PrintWriter, 
		private val enableUpload: Boolean
	) : QueryHandler(rawOut, out) {
    private val fileStart: Int

    init {
        this.fileStart = baseDir.path.length + 1
    }

    private fun fileName(f: File): String {
        if (f.isDirectory) {
            return f.name + '/'
        } else {
            return f.name
        }
    }

    override fun run() {
        startHttpResult(200)
        startHtml("Listing of " + dir)
        val contents = dir.listFiles()
        Arrays.sort(contents) { f1, f2 -> f1.name.compareTo(f2.name) }
        out.println("<table>")
        for (i in contents.indices) {
            val f = contents[i]
            out.println("<tr>")
            if (f.isDirectory) {
                out.print("  <td align=right>-&nbsp;")
            } else {
                val s = fileSizeFormat.format(f.length())
                out.print(" <td align=right>$s bytes")
            }
            out.println("&nbsp;&nbsp;</td>")
            var name = f.name
            if (f.isDirectory) {
                name = name + '/'
            }
            out.print(" <td align=left><a href=\"$name\">")
            out.print(name)
            out.print("</a>")
            out.println("</td>")
            out.println("</tr>")
        }
        out.println("</table>")
        out.println("<br>")
        out.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
        if (contents.size == 1) {
            out.println("${contents.size} entry.<br>")
        } else {
            out.println("${contents.size} entries.<br>")
        }
        if (enableUpload) {
            out.println("""<form action="my_upload" enctype="multipart/form-data" method="post">""")
            out.println("""<p>""")
            out.println("""<p>""")
            out.println("""File to upload:  """)
            out.println("""<input type="file" name="datafile" id="datafile" onChange="fileSelected();">""")
            out.println("""<input type="button" value="Send" onClick="startUploading()">""")
            out.println("""</form>""")
            out.println("""<p id="result"></p>""")
            out.println("""<script>""")
            out.println("""    document.getElementById("result").innerHTML = "";""")
            out.println("")
            out.println("""    function fileSelected() {""")
            out.println("""        var uploadFile = document.getElementById("datafile").files[0];""")
            out.println("""        document.getElementById("result").innerHTML """)
            out.println("""            = "Selected file has length " + uploadFile.size;""")
            out.println("""    }""")
            out.println("")
            out.println("""    var times = 0""")
            out.println("""    function startUploading() {""")
            out.println("""        var xhr = new XMLHttpRequest();""")
            out.println("""        xhr.upload.addEventListener("load", function(e) {""")
            out.println("""            document.getElementById("result").innerHTML = "Done.";""")
            out.println("""        }, false);""")
            out.println("""        xhr.open("POST", document.URL);""")
            out.println("""        xhr.setRequestHeader("X-File-Name", document.getElementById("datafile").files[0].name);""")
            out.println("""        xhr.setRequestHeader("Content-Type", "application/octet-stream")""")
            out.println("""        xhr.overrideMimeType("application/octet-stream");""")
            out.println("""        var uploadFile = document.getElementById("datafile").files[0];""")
            out.println("""        xhr.send(uploadFile)""")
            out.println("""        times++;""")
            out.println("""        document.getElementById("result").innerHTML = "uploading...  " + times;""")
            out.println("""    }""")
            out.println("")
            out.println("""</script>""")
        }
        endHtml()
    }

    companion object {
        private val fileSizeFormat = NumberFormat.getInstance()
    }
}
