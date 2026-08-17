package com.example.paymentsrecording.util

import com.example.paymentsrecording.data.db.entity.Category

/**
 * 根据交易信息（商户、金额、备注）自动识别账单分类。
 * 关键词匹配 + 金额启发式。返回最匹配的支出分类。
 */
object CategoryClassifier {

    private val keywords: Map<String, List<String>> = linkedMapOf(
        "餐饮" to listOf("餐", "饭", "面", "食", "吃", "米其林", "外卖", "美团", "饿了么", "饿了", "肯德基", "麦当劳", "星巴克", "咖啡", "奶茶", "茶饮", "火锅", "烧烤", "寿司", "早餐", "午餐", "晚餐", "夜宵", "小笼", "饺子", "包子", "粥", "粉", "鸡", "鸭", "鱼", "肉", "菜", "厨房", "餐厅", "饭店", "食堂"),
        "交通" to listOf("打车", "滴滴", "出租", "地铁", "公交", "高铁", "火车", "机票", "飞机", "停车", "加油", "油费", "过路费", "ETC", "单车", "骑行", "哈啰", "美团单车", "共享", "12306", "车票"),
        "购物" to listOf("淘宝", "京东", "天猫", "拼多多", "超市", "便利店", "711", "罗森", "全家", "购物", "商城", "商店", "百货", "衣服", "鞋", "化妆品", "护肤", "纸巾", "洗衣", "日用", "京东到家", "叮咚"),
        "娱乐" to listOf("电影", "KTV", "游戏", "网吧", "剧本杀", "密室", "演出", "演唱会", "门票", "游乐园", "酒吧", "台球", "健身", "瑜伽", "spa", "按摩", "电视会员", "会员", "充值", "Steam", "epic"),
        "住房" to listOf("房租", "租金", "物业", "水电", "燃气", "宽带", "网费", "房贷", "装修", "家具", "家私"),
        "医疗" to listOf("药", "医院", "诊所", "挂号", "体检", "看病", "牙科", "眼科", "医保", "健康"),
        "教育" to listOf("学费", "书", "课程", "培训", "考试", "报名", "学习", "教辅", "文具", "网课", "得到", "知乎", "樊登"),
        "通讯" to listOf("话费", "流量", "手机", "充值", "运营商", "移动", "联通", "电信", "10086", "10010"),
        "旅行" to listOf("酒店", "民宿", "旅馆", "住宿", "机票", "度假", "旅游", "旅行", "携程", "去哪儿", "飞猪", "airbnb"),
        "数码" to listOf("手机", "电脑", "笔记本", "耳机", "音箱", "相机", "键盘", "鼠标", "平板", "ipad", "iphone", "华为", "小米", "配件", "数码"),
        "日用" to listOf("纸巾", "洗洁精", "垃圾袋", "洗衣液", "沐浴", "洗发", "牙膏", "毛巾", "日用")
    )

    /**
     * @param text 商户 + 备注合并文本
     * @param expenseCategories 当前支出分类列表
     * @return 最佳匹配分类，无匹配返回 null（由调用方使用默认“其他支出”）
     */
    fun classify(text: String, expenseCategories: List<Category>): Category? {
        if (text.isBlank()) return null
        val lower = text.lowercase()

        for ((catName, words) in keywords) {
            if (words.any { lower.contains(it.lowercase()) }) {
                return expenseCategories.firstOrNull { it.name == catName }
            }
        }
        return null
    }

    /** 金额启发：>2000 更像数码/购物，小数点 .99 常见餐饮零售，这里仅作占位扩展点。 */
    fun hintByAmount(amount: Double, expenseCategories: List<Category>): Category? {
        return when {
            amount >= 3000 -> expenseCategories.firstOrNull { it.name == "数码" }
            else -> null
        }
    }
}
