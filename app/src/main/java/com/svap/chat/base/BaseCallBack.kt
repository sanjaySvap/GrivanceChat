package com.svap.chat.base

interface BaseCallBack<T> {
    fun getCallbackItem(item:T,position:Int=0,tag:String="")
}