package com.ly.wvp.data.model

/**
 * 1.云端记录
 * {
 *     "code": 0,
 *     "msg": "成功",
 *     "data": {
 *         "pageNum": 1,
 *         "pageSize": 15,
 *         "size": 5,
 *         "pages": 1,
 *         "total": 5,
 *         "list": [
 *             {
 *                 "app": "rtp",
 *                 "stream": "43000000801320000009_43000000801310000009",
 *                 "time": "2023-10-07 10:09:42"
 *             },
 *             {
 *                 "app": "rtp",
 *                 "stream": "43000000801320000009_43000000801320000009",
 *                 "time": "2023-09-26 15:25:23"
 *             },
 *             {
 *                 "app": "rtp",
 *                 "stream": "43000000801320000008_43000000801310000008",
 *                 "time": "2023-09-18 11:00:26"
 *             },
 *             {
 *                 "app": "rtsp",
 *                 "stream": "43000000801310000008",
 *                 "time": "2023-03-02 13:57:19"
 *             },
 *             {
 *                 "app": "rtsp",
 *                 "stream": "43000000801320000008_43000000801310000008",
 *                 "time": "2023-03-02 10:32:38"
 *             }
 *         ]
 *     }
 * }
 */
class PageInfo<T>() {

    /**
     * 更新到内部数据
     */
    fun update(data: PageInfo<T>) {
        this.pageNum = data.pageNum
        this.pageSize = data.pageSize
        this.size = data.size
        this.total = data.total
        data.list?.let {
            this.list = ArrayList<T>(it)
        }
    }

    //当前页
    var pageNum = 0

    //每页的数量
    var pageSize = 0

    //当前页的数量
    var size = 0

    //总页数
    var pages = 0

    //总数
    var total = 0
    var list: List<T>? = null

}