package bjzhou.coolapk.app.model

class CardEntity {
    var entityType: String? = null
    var title: String? = null
    var url: String? = null
    var description: String? = null
    var pic: String = ""
    var lastupdate: Long = 0

    override fun toString(): String {
        return "CardEntity{" +
                "entityType='" + entityType + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", pic='" + pic + '\'' +
                ", lastupdate=" + lastupdate +
                '}'
    }
}
