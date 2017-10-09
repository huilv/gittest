package com.sulu.kotlin.utils

/**
 * @作者:My
 * @创建日期: 2017/8/9 11:03
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ImageSpan
import com.daunkredit.program.sulu.R.mipmap
import com.sulu.kotlin.data.EmojiLocationBean

/**
 * 使用之前先init
 */
object EmojiManager {

    private val smileResArray = intArrayOf(mipmap.aliwx_s001, mipmap.aliwx_s002, mipmap.aliwx_s003, mipmap.aliwx_s004, mipmap.aliwx_s005, mipmap.aliwx_s006, mipmap.aliwx_s007, mipmap.aliwx_s008, mipmap.aliwx_s009, mipmap.aliwx_s010, mipmap.aliwx_s011, mipmap.aliwx_s012, mipmap.aliwx_s013, mipmap.aliwx_s014, mipmap.aliwx_s015, mipmap.aliwx_s016, mipmap.aliwx_s017, mipmap.aliwx_s018, mipmap.aliwx_s019, mipmap.aliwx_s020, mipmap.aliwx_s021, mipmap.aliwx_s022, mipmap.aliwx_s023, mipmap.aliwx_s024, mipmap.aliwx_s025, mipmap.aliwx_s026, mipmap.aliwx_s027, mipmap.aliwx_s028, mipmap.aliwx_s029, mipmap.aliwx_s030, mipmap.aliwx_s031, mipmap.aliwx_s032, mipmap.aliwx_s033, mipmap.aliwx_s034, mipmap.aliwx_s035, mipmap.aliwx_s036, mipmap.aliwx_s037, mipmap.aliwx_s038, mipmap.aliwx_s039, mipmap.aliwx_s040, mipmap.aliwx_s041, mipmap.aliwx_s042, mipmap.aliwx_s043, mipmap.aliwx_s044, mipmap.aliwx_s045, mipmap.aliwx_s046, mipmap.aliwx_s047, mipmap.aliwx_s048, mipmap.aliwx_s049, mipmap.aliwx_s050, mipmap.aliwx_s051, mipmap.aliwx_s052, mipmap.aliwx_s053, mipmap.aliwx_s054, mipmap.aliwx_s055, mipmap.aliwx_s056, mipmap.aliwx_s057, mipmap.aliwx_s058, mipmap.aliwx_s059, mipmap.aliwx_s060, mipmap.aliwx_s061, mipmap.aliwx_s062, mipmap.aliwx_s063, mipmap.aliwx_s064, mipmap.aliwx_s065, mipmap.aliwx_s066, mipmap.aliwx_s067, mipmap.aliwx_s068, mipmap.aliwx_s069, mipmap.aliwx_s070, mipmap.aliwx_s071, mipmap.aliwx_s072, mipmap.aliwx_s073, mipmap.aliwx_s074, mipmap.aliwx_s075, mipmap.aliwx_s076, mipmap.aliwx_s077, mipmap.aliwx_s078, mipmap.aliwx_s079, mipmap.aliwx_s080, mipmap.aliwx_s081, mipmap.aliwx_s082, mipmap.aliwx_s083, mipmap.aliwx_s084, mipmap.aliwx_s085, mipmap.aliwx_s086, mipmap.aliwx_s087, mipmap.aliwx_s088, mipmap.aliwx_s089, mipmap.aliwx_s090, mipmap.aliwx_s091, mipmap.aliwx_s092, mipmap.aliwx_s093, mipmap.aliwx_s094, mipmap.aliwx_s095, mipmap.aliwx_s096, mipmap.aliwx_s097, mipmap.aliwx_s098, mipmap.aliwx_s099)
    // private val gifResArray = intArrayOf(mipmap.aliwx_g002, mipmap.aliwx_g003, mipmap.aliwx_g004, mipmap.aliwx_g005, mipmap.aliwx_g006, mipmap.aliwx_g007, mipmap.aliwx_g008, mipmap.aliwx_g009, mipmap.aliwx_g010, mipmap.aliwx_g011, mipmap.aliwx_g012, drawable.aliwx_g013, drawable.aliwx_g014, drawable.aliwx_g015, drawable.aliwx_g016, drawable.aliwx_g017, drawable.aliwx_g018, drawable.aliwx_g019, drawable.aliwx_g020, drawable.aliwx_g021)
    private val shortCuts = arrayOf("/:^_^", "/:^$^", "/:Q", "/:815", "/:809", "/:^O^", "/:081", "/:087", "/:086", "/:H", "/:012", "/:806", "/:b", "/:^x^", "/:814", "/:^W^", "/:080", "/:066", "/:807", "/:805", "/:071", "/:072", "/:065", "/:804", "/:813", "/:818", "/:015", "/:084", "/:801", "/:811", "/:?", "/:077", "/:083", "/:817", "/:!", "/:068", "/:079", "/:028", "/:026", "/:007", "/:816", "/:\'\"\"", "/:802", "/:027", "/:(Zz...)", "/:*&*", "/:810", "/:>_<", "/:018", "/:>O<", "/:020", "/:044", "/:819", "/:085", "/:812", "/:\"", "/:>M<", "/:>@<", "/:076", "/:069", "/:O", "/:067", "/:043", "/:P", "/:808", "/:>W<", "/:073", "/:008", "/:803", "/:074", "/:O=O", "/:036", "/:039", "/:045", "/:046", "/:048", "/:047", "/:girl", "/:man", "/:052", "/:(OK)", "/:8*8", "/:)-(", "/:lip", "/:-F", "/:-W", "/:Y", "/:qp", "/:$", "/:%", "/:(&)", "/:@", "/:~B", "/:U*U", "/:clock", "/:R", "/:C", "/:plane", "/:075")
    private val meanings = arrayOf("微笑", "害羞", "吐舌头", "偷笑", "爱慕", "大笑", "跳舞", "飞吻", "安慰", "抱抱", "加油", "胜利", "强", "亲亲", "花痴", "露齿笑", "查找", "呼叫", "算账", "财迷", "好主意", "鬼脸", "天使", "再见", "流口水", "享受", "色情狂", "呆若木鸡", "思考", "迷惑", "疑问", "没钱了", "无聊", "怀疑", "嘘", "小样", "摇头", "感冒", "尴尬", "傻笑", "不会吧", "无奈", "流汗", "凄凉", "困了", "晕", "忧伤", "委屈", "悲泣", "大哭", "痛哭", "I服了U", "对不起", "再见", "皱眉", "好累", "生病", "吐", "背", "惊讶", "惊愕", "闭嘴", "欠扁", "鄙视你", "大怒", "生气", "财神", "学习雷锋", "恭喜发财", "小二", "老大", "邪恶", "单挑", "CS", "隐形人", "炸弹", "惊声尖叫", "漂亮MM", "帅哥", "招财猫", "成交", "鼓掌", "握手", "红唇", "玫瑰", "残花", "爱心", "心碎", "钱", "购物", "礼物", "收邮件", "电话", "举杯庆祝", "时钟", "等待", "很晚了", "飞机", "支付宝")

    private val GIFDOMAIN = "http://download.taobaocdn.com/tbsc/client/taotoy/"
    private val NEW_GIF_PATHS = arrayOf("002.gif", "003.gif", "004.gif", "005.gif", "006.gif", "007.gif", "008.gif", "009.gif", "010.gif", "011.gif", "012.gif", "013.gif", "014.gif", "015.gif", "016.gif", "017.gif", "018.gif", "019.gif", "020.gif", "021.gif")
    private val GIF_SHORT_CUTS = arrayOf("T1H_u4XhhrXXXXXXXX.gif", "T19FW5XjRlXXXXXXXX.gif", "T19t55XaXcXXXXXXXX.gif", "T1eU54Xa0oXXXXXXXX.gif", "T1jdu5XhxdXXXXXXXX.gif", "T1ST14XiXqXXXXXXXX.gif", "T1Dta5XgVeXXXXXXXX.gif", "T1wUq4XjNpXXXXXXXX.gif", "T1Xc55XnpeXXXXXXXX.gif", "T1XDO4Xo4qXXXXXXXX.gif", "T1pcu5XcpgXXXXXXXX.gif", "T1S9q4XhltXXXXXXXX.gif", "T11YG5XlVhXXXXXXXX.gif", "T12mi4XkFtXXXXXXXX.gif", "T1p994XchsXXXXXXXX.gif", "T1SX15XhllXXXXXXXX.gif", "T1zJ55XaVcXXXXXXXX.gif", "T1bU94XX0nXXXXXXXX.gif", "T1VaO5XkVjXXXXXXXX.gif", "T1eJq5XkddXXXXXXXX.gif", "T1CJe5Xd0eXXXXXXXX.gif")
    private val GIF_SHORT_CUTS_DAILY = arrayOf("T1IYpcXh4IXXXXXXXX.gif", "T1gbJcXlxXXXXXXXXX.gif", "T16HlcXh8KXXXXXXXX.gif", "T1aHVcXalbXXXXXXXX.gif", "T1UYVcXfFeXXXXXXXX.gif", "T1NHlcXdNJXXXXXXXX.gif", "T15HlcXdxJXXXXXXXX.gif", "T15blcXdxJXXXXXXXX.gif", "T1jrpcXddJXXXXXXXX.gif", "T1YYlcXdFJXXXXXXXX.gif", "T1HrpcXaVIXXXXXXXX.gif", "T1FblcXd0JXXXXXXXX.gif", "T1xYlcXd8JXXXXXXXX.gif", "T1uYpcXdNJXXXXXXXX.gif", "T13YpcXflLXXXXXXXX.gif", "T16HlcXiXKXXXXXXXX.gif", "T1YHJcXeBbXXXXXXXX.gif", "T1_HpcXcNIXXXXXXXX.gif", "T1AHlcXd4JXXXXXXXX.gif", "T18blcXdJJXXXXXXXX.gif", "T1SHlcXmlKXXXXXXXX.gif")
    private val GIF_PC_MD5_KEY = arrayOf("33bd5c9cd7038e2203d6ce6b91179ac3", "adb95dbf710753c2dc3709ea24d22b1c", "cf8d90b3925fe105b7c534b7afb886bd", "a0437f0b83879d5313958882ac744943", "1f2ed4c7f96a45849b6f01297bcbd8aa", "473aa840fd9fd6c91805df366e91eb23", "0cb4709242b2c54e36b1ade23ac22edb", "c03e4fe8f08b4ad508e8d73f6db3a040", "82a06562fb41763f15731ed243772fee", "1c9fa391712bc821b41acbe3b0e3c95c", "429c3c36c2aabffaeaecdb9d877a8f85", "a348c99a827ec36b881fcfc44557efdc", "fa12e9d97be71ea19fa759c1015afc61", "f19969c69e5f16f635a12ab693a3502e", "5bc9b3382f77da0e8a312118122ebd89", "758060f83a6c0aa8e0a574ab7f1c5c8e", "c602a2bd9e0dffb3b57163e158a82e49", "393939c13a3c7571413c9443fc22ed04", "ce50b3fc08e55f47fe3c9a0c6fc0e868", "00ba58aa32693fcd49ee4c19325de58d")
    private val bound by lazy { mCtx!!.dp2Px(20) }
    private var mCtx: Context? = null
    fun init(ctx: Context) {
        if (mCtx == null) {
            mCtx = ctx
        }
    }

    fun resourceIdToString(resourId: Int): String {
        val indexOf = smileResArray.indexOf(resourId)
        return shortCuts[indexOf]
    }

    fun stringToResourceId(s: String): Int {
        val indexOf = shortCuts.indexOf(s)
        return smileResArray[indexOf]
    }

    fun getResultStringByContent(content: String): CharSequence {

        if (content.contains("/:")) {
            val split = content.split("/")
            val normalString = ArrayList<String>()
            val emojiList = ArrayList<EmojiLocationBean>()
            var currentLength = 0
            var emojiStr: String? = null;
            split.forEach {
                if (!it.startsWith(":")) {
                    if (normalString.size <= 0) {
                        normalString.add(it)
                        currentLength += it.length
                    } else {
                        normalString.add("/" + it)
                        currentLength += (it.length + 1)
                    }

                } else {
                    emojiStr = emojigetContainedEmoji("/" + it)
                    if (emojiStr != null) {
                        emojiList.add(EmojiLocationBean(stringToResourceId(emojiStr!!), currentLength, ++currentLength))
                        if (("/" + it).length > emojiStr!!.length) {
                            val substring = it.substring(emojiStr!!.length - 1)
                            normalString.add(substring)
                            currentLength += substring.length
                        }
                        emojiStr = null

                    } else {
                        normalString.add("/" + it)
                        currentLength += (it.length + 1)
                    }
                }
            }
            val result = getResultString(normalString)

            val spanBuilder = SpannableStringBuilder(result)
            emojiList.forEach {
                spanBuilder.insert(it.startIndex, "\u0000")
                spanBuilder.setSpan(ImageSpan(getDrawable(it.resourid)),
                        it.startIndex, it.endIndex,
                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            return spanBuilder
        } else {
            return content
        }
    }

    private fun getResultString(normalString: ArrayList<String>): CharSequence? {
        val sb = StringBuilder()
        normalString.forEach {
            sb.append(it)
        }
        return sb.toString()
    }

    private fun emojigetContainedEmoji(s: String): String? {
        shortCuts.forEach {
            if (s.startsWith(it)) {
                return if (TextUtils.equals(it, "/:O") && s.startsWith("/:O=O")) {
                    "/:O=O"
                } else {
                    it
                }
            }
        }
        return null
    }

    private fun getDrawable(resourid: Int): Drawable? {
        val drawable = mCtx!!.resources.getDrawable(resourid)
        drawable.bounds = Rect(0, 0, bound.toInt(), bound.toInt())
        return drawable
    }
}