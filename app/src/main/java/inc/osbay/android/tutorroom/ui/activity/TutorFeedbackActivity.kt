package inc.osbay.android.tutorroom.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

/**
 * A simple [Fragment] subclass.
 */
class TutorFeedbackActivity : Activity(), View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    internal var st: Array<String>
    //private FeedbackAdapter mFeedbackAdapter;

    //private LinearLayout mLlPagerCount;
    private var mRlTutorSubmit: RelativeLayout? = null
    private var mRlTutorFeedback: RelativeLayout? = null
    //private TextView mTvFeedbackNotice;
    private var mTvRatingFeedback: TextView? = null
    private var mEtStudentMsg: EditText? = null
    private var mBtnFeedbackSubmit: TextView? = null
    private var mRbOtherFeedbackRating: RatingBar? = null
    private val dots: Array<ImageView>? = null
    private val dotsCount: Int = 0
    //private float[] mRatingValues;
    //private String[] mCommentValues;
    private var mRatingValue: Float = 0.toFloat()
    private var mCommentValue: String? = null
    private val mPosition = 0
    private var mTutorFeedback = ""
    private var mStClassroomId: String? = null
    private var rbFeedbackRating: RatingBar? = null

    @SuppressLint("ClickableViewAccessibility")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_feedback)

        st = resources.getStringArray(R.array.tutor_feedback_type)

        mStClassroomId = intent.getStringExtra(CLASSROOM_ID)

        //mTvFeedbackNotice = findViewById(R.id.tv_feedback_notice);
        mTvRatingFeedback = findViewById(R.id.tv_rating_feedback)
        mEtStudentMsg = findViewById(R.id.et_student_msg)
        mRbOtherFeedbackRating = findViewById(R.id.rb_other_feedback_rating)
        val rbTutorRating = findViewById<RatingBar>(R.id.rb_tutor_rating)
        mRlTutorSubmit = findViewById(R.id.rl_tutor_submit)
        mRlTutorFeedback = findViewById(R.id.rl_tutor_feedback)
        //mLlPagerCount = findViewById(R.id.ll_pager_counts);
        mBtnFeedbackSubmit = findViewById(R.id.btn_feedback_submit)
        val vwFeedback = findViewById<LinearLayout>(R.id.vw_feedback)
        val tvTutorFeedback = findViewById<TextView>(R.id.tv_tutor_feedback)
        rbFeedbackRating = findViewById(R.id.rb_feedback_rating)

        val rlMainTutorFeedback = findViewById<RelativeLayout>(R.id.rl_main_tutor_feedback)
        rlMainTutorFeedback.setOnTouchListener { view, motionEvent ->
            hideKeyboard()
            false
        }

        vwFeedback.setOnTouchListener { view, motionEvent ->
            hideKeyboard()
            false
        }
        mBtnFeedbackSubmit!!.setOnClickListener(this)

        tvTutorFeedback.text = getString(R.string.tu_tutor_feedback)

        /*mRatingValues = new float[st.length];
        mCommentValues = new String[st.length];*/
        rbTutorRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            mRlTutorFeedback!!.visibility = View.GONE
            mRlTutorSubmit!!.visibility = View.VISIBLE
            mRatingValue = rating
            rbFeedbackRating!!.rating = mRatingValue

            if (rating >= 2.5f) {
                mTutorFeedback = getString(R.string.tu_star_3_5)
                mTvRatingFeedback!!.text = getString(R.string.tu_star_3_5)
            } else {
                mTutorFeedback = getString(R.string.tu_star_1_2)
                mTvRatingFeedback!!.text = getString(R.string.tu_star_1_2)
            }
        }

        /*mFeedbackAdapter = new FeedbackAdapter();
        vwFeedback.setAdapter(mFeedbackAdapter);
        vwFeedback.addOnPageChangeListener(this);*/
        mRbOtherFeedbackRating!!.onRatingBarChangeListener = this
        //setUiPageViewController();
    }

    public override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {}

    /*private void setUiPageViewController() {
        dotsCount = mFeedbackAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            mLlPagerCount.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }*/

    /*@Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));

        if (position == 0) {
            mEtStudentMsg.setText(mCommentValues[position]);
            mTvRatingFeedback.setText(mTutorFeedback);
            mRbOtherFeedbackRating.setVisibility(View.GONE);
            mTvFeedbackNotice.setVisibility(View.VISIBLE);
            mBtnFeedbackSubmit.setVisibility(View.VISIBLE);
        } else if (position == 1) {
            mEtStudentMsg.setText(mCommentValues[position]);
            mRbOtherFeedbackRating.setRating(mRatingValues[position]);
            mRbOtherFeedbackRating.setVisibility(View.VISIBLE);
            mTvFeedbackNotice.setVisibility(View.GONE);
            mTvRatingFeedback.setText(getString(R.string.tu_call_quality));
            mBtnFeedbackSubmit.setVisibility(View.GONE);
        } else if (position == 2) {
            mEtStudentMsg.setText(mCommentValues[position]);
            mRbOtherFeedbackRating.setRating(mRatingValues[position]);
            mRbOtherFeedbackRating.setVisibility(View.VISIBLE);
            mTvFeedbackNotice.setVisibility(View.GONE);
            mTvRatingFeedback.setText(getString(R.string.tu_feedback_content));
            mBtnFeedbackSubmit.setVisibility(View.GONE);
        } else {
            mEtStudentMsg.setText(mCommentValues[position]);
            mRbOtherFeedbackRating.setVisibility(View.VISIBLE);
            mRbOtherFeedbackRating.setVisibility(View.GONE);
            mTvFeedbackNotice.setVisibility(View.GONE);
            mTvRatingFeedback.setText(getString(R.string.tu_feedback_others));
            mBtnFeedbackSubmit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mCommentValues[mPosition] = mEtStudentMsg.getText().toString();
    }*/

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_feedback_submit -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage(getString(R.string.loading))
                progressDialog.setCancelable(false)
                progressDialog.show()
                mCommentValue = mEtStudentMsg!!.text.toString()
                val serverRequestManager = ServerRequestManager(this)
                if (mStClassroomId != null) {
                    val sharedPreferenceData = SharedPreferenceData(this@TutorFeedbackActivity)
                    val accountID = sharedPreferenceData.getInt("account_id").toString()
                    serverRequestManager.sendFeedback(accountID, mCommentValue, mStClassroomId, mRatingValue.toString(), object : ServerRequestManager.OnRequestFinishedListener {
                        override fun onSuccess(result: ServerResponse?) {
                            if (this@TutorFeedbackActivity != null) {
                                progressDialog.dismiss()

                                val mainActivity = Intent(this@TutorFeedbackActivity, MainActivity::class.java)
                                val bundle = Bundle()
                                bundle.putString("class_type", "normal")
                                mainActivity.putExtras(bundle)
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(mainActivity)

                                finish()
                            }
                        }

                        override fun onError(err: ServerError) {
                            if (this@TutorFeedbackActivity != null) {
                                progressDialog.dismiss()
                                Toast.makeText(this@TutorFeedbackActivity, err.message, Toast.LENGTH_SHORT).show()

                                val mainActivity = Intent(this@TutorFeedbackActivity, MainActivity::class.java)
                                val bundle = Bundle()
                                bundle.putString("class_type", "normal")
                                mainActivity.putExtras(bundle)
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(mainActivity)

                                finish()
                            }
                        }
                    })
                } else {
                    progressDialog.dismiss()
                    finish()
                }
            }
        }
    }

    override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
        mRatingValue = rating
        rbFeedbackRating!!.rating = mRatingValue
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEtStudentMsg!!.windowToken, 0)
    }

    companion object {

        val CLASSROOM_ID = "TutorFeedbackActivity.CLASSROOM_ID"
    }

    /*private class FeedbackAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return st.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_vp_feedback, null);

            TextView tvFeedbackTitle = v.findViewById(R.id.tv_feedback_title);
            tvFeedbackTitle.setText(st[position]);
            SimpleDraweeView sdvFeedback = v.findViewById(R.id.sdv_feedback_tutor_photo);
            RatingBar rbFeedbackRating = v.findViewById(R.id.rb_feedback_rating);
            ImageView imvOtherFeedback = v.findViewById(R.id.imv_other_feedback);
            if (position == 0) {
                sdvFeedback.setVisibility(View.VISIBLE);
                imvOtherFeedback.setVisibility(View.GONE);
                rbFeedbackRating.setVisibility(View.VISIBLE);
                rbFeedbackRating.setRating(mRatingValues[0]);
            } else if (position == 1) {
                sdvFeedback.setVisibility(View.GONE);
                imvOtherFeedback.setVisibility(View.VISIBLE);
                imvOtherFeedback.setImageResource(R.mipmap.ic_call_quality);
                rbFeedbackRating.setVisibility(View.GONE);
            } else if (position == 2) {
                imvOtherFeedback.setVisibility(View.VISIBLE);
                sdvFeedback.setVisibility(View.GONE);
                imvOtherFeedback.setImageResource(R.mipmap.ic_content);
                rbFeedbackRating.setVisibility(View.GONE);
            } else {
                imvOtherFeedback.setVisibility(View.VISIBLE);
                sdvFeedback.setVisibility(View.GONE);
                imvOtherFeedback.setImageResource(R.mipmap.ic_others);
                rbFeedbackRating.setVisibility(View.GONE);
            }

            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }*/
}// Required empty public constructor