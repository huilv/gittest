package com.sulu.kotlin.data

import com.alibaba.mobileim.conversation.YWMessage
import com.daunkredit.program.sulu.app.App
import com.daunkredit.program.sulu.bean.YWUser
import com.sulu.kotlin.utils.EmojiManager
import com.sulu.kotlin.utils.timeLongToString
import java.io.Serializable

/**
 * @作者:My
 * @创建日期: 2017/7/17 9:43
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
data class LoanRange(var amountStep:Double, var interestRate:Double,var maxAmount:Double,var maxPeriod:Double,var minAmount:Double,var
                     minPeriod:Double,var periodStep:Double,var periodUnit:String,var serviceFee:Double):Serializable
class MessageBean:Serializable{
    var fromMe: Boolean = false
    var time: String = ""
    var message: CharSequence = ""
    var type:Int = 0
    constructor(){}
    constructor(ywMessage: YWMessage){
        fromMe = !ywMessage.authorId.contains(App.SERVICE_ACCOUNT)
        message = EmojiManager.getResultStringByContent(ywMessage.content)
        time = timeLongToString(ywMessage.timeInMillisecond)
        type = ywMessage.subType
    }
}

data class FunctionInfo(var name:String, var iconId:Int, val func:()->Any):Serializable

data class EmojiLocationBean(val resourid:Int,val startIndex:Int,val endIndex:Int):Serializable

data class ActivityCenterBean(val detailUrl: String,val iconPath:String,val content:String,val time:String):Serializable

/**
 * {
"type": "DISCHARGE_INTEREST",
"description": "string",
"dischargeInterestDay": 0,
"id": 0,
"title": "string",
"used": true,
"validBeginTime": "2017-08-16T08:27:02.611Z",
"validEndTime": "2017-08-16T08:27:02.612Z"
}
 */
data class CouponBean(val type:String, val description:String, val dischargeInterestDay:Int, val id:Long, val title:String, val used:Boolean, val validBeginTime:String, val validEndTime:String):Serializable

/**
{
"completeLoanApplyCount": 0,
"inviteeCount": 0
}
 */
data class InviteeBean(val completeLoanApplyCount:Int,val inviteeCount:Int):Serializable

/**
{
    "availableCouponCount": 0,
    "banner": [
    {
        "url": "string"
    }
    ],
    "chatAccount": {
    "password": "string",
    "userid": "string"
},
    "inviteeCount": 0,
    "name": "string"
}
*/
data class ActivityInfoBean(val url:String):Serializable

data class MeInfoBean(val availableCouponCount:Int,var banner:ArrayList<ActivityInfoBean>?,val inviteeCount: Int,val name: String?,val chatAccount: YWUser?):Serializable{
    constructor() : this(0,null,0,null,null)
}

/**
[
{
"mobile": "string",
"realName": "string",
"registerTime": "2017-08-25T01:27:58.647Z"
}
]
 */
data class InviteePersonBean(val mobile:String,val realName:String,val registerTime:String):Serializable

data class InviteResult(val list: java.util.ArrayList<InviteePersonBean>, val bean:InviteeBean, val code: String):Serializable