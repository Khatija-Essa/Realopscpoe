package com.example.realopsc


class model() {
    var title: String? = null
    var description: String? = null
    var imageUrl: String? = null
    var date: String? = null
    var start: String? = null
    var end: String? = null

    constructor(title: String?, description: String?, imageUrl: String?, date: String?, start: String?, end: String?) : this() {
        this.title = title
        this.description = description
        this.imageUrl = imageUrl
        this.date = date
        this.start = start
        this.end = end
    }
}
