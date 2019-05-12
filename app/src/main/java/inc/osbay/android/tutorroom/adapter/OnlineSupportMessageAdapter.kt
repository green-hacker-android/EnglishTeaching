package inc.osbay.android.tutorroom.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.ChatMessage

class OnlineSupportMessageAdapter(private val chatMessages: List<ChatMessage>, private val context: Context, private val accountID: String) : RecyclerView.Adapter<OnlineSupportMessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_chat_support, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var position = position
        val msg = chatMessages[chatMessages.size - ++position]
        //Log.e("OnlineSupport", "account id - " + accountId + ", sender - " + msg.getSender());

        // Messages sent by Student
        if (accountID == msg.sender!!.split("_".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]) {
            holder.llChatLayout.gravity = Gravity.END
            holder.imvSupportIcon.visibility = View.GONE
            holder.tvSendMessage.text = msg.body
            holder.tvSendMessage.visibility = View.VISIBLE
            holder.tvReceivedMessage.visibility = View.GONE
        }// Messages sent by Consultant or Call Center Manager
        else {
            holder.llChatLayout.gravity = Gravity.START
            holder.tvSendMessage.visibility = View.GONE
            /*if (msg.getMessageType().equalsIgnoreCase(TRIAL_MESSAGE_TYPE)
                    || msg.getMessageType().equalsIgnoreCase(NORMAL_MESSAGE_TYPE)
                    || msg.getMessageType().equalsIgnoreCase("")) {*/
            //holder.trialConfirmLayout.setVisibility(View.GONE);
            holder.tvReceivedMessage.text = msg.body
            holder.tvReceivedMessage.visibility = View.VISIBLE
            holder.imvSupportIcon.visibility = View.VISIBLE
            holder.llChatLayout.visibility = View.VISIBLE
            //}
            /*if (msg.getMessageType().equalsIgnoreCase(TRIAL_COMFIRM_TYPE)) {
                    //FlurryAgent.logEvent("Trial today scheduled");
                    holder.trialConfirmLayout.setVisibility(View.VISIBLE);
                    holder.llChatLayout.setVisibility(View.GONE);
                    String messageContent = msg.getBody();
                    try {
                        JSONObject msgJson = new JSONObject(messageContent);
                        final String bookStartDateTime = msgJson.getString("bookstart_datetime");
                        String bookEndDateTime = msgJson.getString("bookend_datetime");
                        String tutorName = msgJson.getString("tutor_name");
                        String tutorAvatar = msgJson.getString("tutor_avatar");

                        holder.tutorName.setText(tutorName);
                        holder.tutorImg.setImageURI(tutorAvatar);
                        holder.scheduleDate.setText(convertUTCToLocale(bookStartDateTime));
                        holder.scheduleTime.setText(getStartTimeFromUTC(bookStartDateTime) + " - " + getStartTimeFromUTC(bookEndDateTime));
                        if (isScheduledTrialClassExpired(bookEndDateTime)) {
                            holder.confirm.setClickable(false);
                            holder.confirm.setTextColor(getResources().getColor(R.color.view_light_gray));
                            holder.confirm.setBackground(getResources().getDrawable(R.drawable.trial_class_confirm_btn_disabled_bg));
                        } else {
                            holder.confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FlurryAgent.logEvent("Click confirm trial today");
                                    String token = prefs.getString("access_token", null);
                                    String accountId = prefs.getString("account_id", null);

                                    EditNumberFragment editNumberFragment = new EditNumberFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EditNumberFragment.sourceFragment, OnlineSupportFragment.class.getSimpleName());
                                    editNumberFragment.setArguments(bundle);

                                    if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(token)) {
                                        AccountAdapter accountAdapter = new AccountAdapter(getActivity());
                                        Account account = accountAdapter.getAccountById(accountId);
                                        if (account.getStatus() == Account.Status.REQUEST_TRIAL && TextUtils.isEmpty(account.getPhoneNumber())) {
                                            getFragmentManager().beginTransaction()
                                                    .replace(R.id.fl_trial_submit, editNumberFragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        } else {
                                            getActivity().finish();
                                        }
                                    } else {
                                        getFragmentManager().beginTransaction()
                                                .replace(R.id.fl_trial_submit, editNumberFragment)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }*/
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReceivedMessage: TextView
        val tvSendMessage: TextView
        val llChatLayout: LinearLayout
        val imvSupportIcon: ImageView

        init {

            imvSupportIcon = itemView.findViewById(R.id.imv_support_icon)
            llChatLayout = itemView.findViewById(R.id.ll_chat_layout)
            tvReceivedMessage = itemView.findViewById(R.id.tv_receive_message)
            tvSendMessage = itemView.findViewById(R.id.tv_send_message)
        }
    }
}
