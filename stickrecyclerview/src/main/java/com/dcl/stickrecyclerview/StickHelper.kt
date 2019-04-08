package com.dcl.stickrecyclerview

/**
 * Created by dcl@yuni on 2019/4/8.
 */
interface StickHelper {
    /**
     * 是否是需要悬浮的item
     */
    fun isFloatType(position: Int): Boolean

    /**
     * 是否是悬浮item之下的item类型
     */
    fun isFloatMembers(position: Int): Boolean
}