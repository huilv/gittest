package com.daunkredit.program.sulu.app.custom;

/**
 * @作者:My
 * @创建日期: 2017/3/20 16:43
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

//public class ChattingCustomAdvice extends IMChattingPageUI {
//    public ChattingCustomAdvice(Pointcut pointcut) {
//        super(pointcut);
//    }
//
//    /**
//      设置消息气泡背景图，需要.9图
//     * @param conversation 当前消息所在会话
//     * @param message      需要设置背景的消息
//     * @param self         是否是自己发送的消息，true：自己发送的消息， false：别人发送的消息
//     * @return  0: 默认背景 －1:透明背景（无背景） >0:使用用户设置的背景图
//     */
//    @Override
//    public int getMsgBackgroundResId(YWConversation conversation, YWMessage message, boolean self) {
//        int msgType = message.getSubType();
//        if (msgType == YWMessage.SUB_MSG_TYPE.IM_TEXT || msgType == YWMessage.SUB_MSG_TYPE.IM_AUDIO) {
//            if (self) {
//                return R.drawable.talk_right;
//            } else {
//                return R.drawable.talk_left;
//            }
//        } else if (msgType == YWMessage.SUB_MSG_TYPE.IM_IMAGE) {
//            if (self) {
//                return R.drawable.talk_right;
//            } else {
//                return R.drawable.talk_left;
//            }
//        } else if (msgType == YWMessage.SUB_MSG_TYPE.IM_VIDEO) {
//            if (self) {
//                return R.drawable.talk_right;
//            } else {
//                return R.drawable.talk_left;
//            }
//        } else if (msgType == YWMessage.SUB_MSG_TYPE.IM_GEO) {
//            if (self) {
//                return R.drawable.talk_right;
//            } else {
//                return R.drawable.talk_left;
//            }
//        } else if (msgType == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS || msgType == YWMessage.SUB_MSG_TYPE.IM_TRIBE_CUS) {
//            if (self) {
//                return -1;
//            } else {
//                return -1;
//            }
//        }
//        return super.getMsgBackgroundResId(conversation, message, self);
//    }
//
//    /**
//     * 返回自定义聊天输入框高度(单位为dp)
//     * @return
//     *     如果返回值小于等于0,则使用默认值
//     */
//    @Override
//    public int getCustomChattingInputEditTextHeight() {
//        return 0;
//    }
//
//    /**
//     * 返回自定义ChattingReplyBar高度(单位为dp)
//     * @return
//     *     如果返回值小于等于0,则使用默认值
//     */
//    @Override
//    public int getCustomChattingReplyBarHeight() {
//        return 0;
//    }
//
//    /**
//     * 是否隐藏底部ChattingReplyBar
//     * @param conversation
//     * @return
//     */
//    @Override
//    public boolean needHideChattingReplyBar(YWConversation conversation) {
//        return false;
//    }
//
////    @Override
////    public int getDefaultHeadImageResId() {
////        return R.drawable.ic_bca;
////    }
//
//
//
//    @Override
//    public View getCustomTitleView(final Fragment fragment, Context context, LayoutInflater inflater, YWConversation conversation) {
//        View view = inflater.inflate(R.layout.sub_main_activity_top,null);
//        View back = view.findViewById(R.id.id_imagebutton_back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fragment.getActivity().finish();
//            }
//        });
//        view.findViewById(R.id.id_imagebutton_info_list).setVisibility(View.GONE);
//        TextView title = (TextView) view.findViewById(R.id.id_textview_title);
//        title.setText(fragment.getResources().getText(R.string.text_title_customer_hot_line));
//        return view;
//    }
//
//    @Override
//    public int getCustomTextColor(YWConversation conversation, boolean isSelf, int type) {
//        return R.color.color_text_gray;
//    }
//
//
//    /**
//     * 是否隐藏底部ChattingReplyBar
//     *
//     * @return
//     */
//    @Override
//    public boolean needHideChattingReplyBar() {
//        return false;
//    }
//
//
//    /**
//     * 是否隐藏表情发送入口
//     *
//     * @return true:隐藏表情按钮
//     * false:显示表情按钮
//     */
//    @Override
//    public boolean needHideFaceView() {
//        return false;
//    }
//
//    /**
//     * 是否隐藏语音发送入口
//     *
//     * @return true:隐藏语音发送按钮
//     * false:显示语音发送按钮
//     */
//    @Override
//    public boolean needHideVoiceView() {
//        return false;
//    }
//
//
//    /**
//     * 返回表情按钮图标背景资源Id
//     * @return
//     */
//    @Override
//    public int getFaceViewBgResId() {
//        return 0;
//    }
//
//    /**
//     * 返回"+号"按钮选中图标背景资源Id
//     * @return
//     */
//    @Override
//    public int getExpandViewCheckedBgResId() {
//        return 0;
//    }
//
//    /**
//     * 返回"+号"按钮取消选中图标背景资源Id
//     * @return
//     */
//    @Override
//    public int getExpandViewUnCheckedBgResId() {
//        return 0;
//    }
//
//    /**
//     * 返回发送按钮背景资源Id
//     * @return
//     */
//    @Override
//    public int getSendButtonBgId() {
//        return R.drawable.selector_button_primary;
//    }
//
//
//    /**
//     * 修改ChattingReplyBar上的item，可以修改属性或者新增。如果开发者相同类型的会话plugin一样，则可以缓存pluginItems，
//     * 在该方法调用时直接用缓存pluginItems替换掉参数中的pluginItems，替换方式为先removeAll，再addAll
//     * <p>可以修改的属性包括：
//     *  <p>顺序</p>
//     *  <p>是否显示({@link YWInputViewPlugin#setNeedHide(boolean)}),</p>
//     *  <p>Item对应的View的基本操作,</p>
//     * </p>
//     * <p>
//     *     SDK默认按钮的id为：
//     *     <p>{@link YWChattingPlugin.ReplyBarPlugin#VOICE_VIEW}
//     *     <p>{@link YWChattingPlugin.ReplyBarPlugin#INPUT_EDIT_TEXT}
//     *     <p>{@link YWChattingPlugin.ReplyBarPlugin#FACE_VIEW}
//     *     <p>{@link YWChattingPlugin.ReplyBarPlugin#EXPAND_VIEW}
//     *
//     * <p/>
//     * 如果新增则创建一个{@link YWInputViewPlugin}并添加到replyBarItems。
//     * @param conversation ChattingReplyBar所在会话,如果开发者需要可以根据会话类型对ChattingReplyBar的item进行调整
//     * @param pluginItems item列表，初始是包含sdk默认提供的4个item：语音按钮、输入框、表情按钮、”+号“按钮，顺序索引为0,1,2,3
//     * @return 使用sdk默认顺序则无需任何操作
//     */
//    @Override
//    public List<YWInputViewPlugin> adjustCustomInputViewPlugins(final Fragment fragment, YWConversation conversation, List<YWInputViewPlugin> pluginItems) {
//                if (pluginItems != null && pluginItems.size() > 0) {
//                    //对默认项进行操作，可以使用id判断具体是哪一个
//                    for (YWInputViewPlugin plugin : pluginItems) {
//                        if (plugin.getId() == YWChattingPlugin.ReplyBarPlugin.VOICE_VIEW) {
//                            plugin.setNeedHide(true);//隐藏语音输入按钮
//                        }
//                        if (plugin.getId() == YWChattingPlugin.ReplyBarPlugin.EXPAND_VIEW) {
//                            plugin.setIndex(0);
//                        }
//
//                    }
//                    //自定义新增的plugin只需要在布局中设置marginLeft即可
////                    final View plugin = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.demo_extra_item_layout, null);
////                    //TODO 新增项id必须从4开始
////                    final YWInputViewPlugin pluginToAdd = new YWInputViewPlugin(plugin, 4);
////                    plugin.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            Toast.makeText(fragment.getActivity(), "你点击了Id为:" + pluginToAdd.getId() + "的新增item", Toast.LENGTH_LONG).show();
////                        }
////                    });
////                    pluginToAdd.setIndex(0);//在设置index时，如果新增项和默认pluginItem一样则开发者添加的显示在前
////                    pluginItems.add(pluginToAdd);
//                }
//        return pluginItems;
//    }
//
//}
