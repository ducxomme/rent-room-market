package com.example.huuduc.intership_project.ui.activity.room_detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.huuduc.intership_project.R;
import com.example.huuduc.intership_project.data.model.Comment;
import com.example.huuduc.intership_project.data.model.Room;
import com.example.huuduc.intership_project.ui.adapter.CommentAdapter;
import com.example.huuduc.intership_project.ui.base.BaseActivity;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RoomDetailActivity extends BaseActivity implements IRoomDetailView , BaseSliderView.OnSliderClickListener
                , ViewPagerEx.OnPageChangeListener{

    @BindView(R.id.slider)
    SliderLayout slider;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnEdit)
    ImageView btnEdit;
    @BindView(R.id.rating)
    SimpleRatingBar rating;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvArea)
    TextView tvArea;
    @BindView(R.id.tvRoomEmpty)
    TextView tvRoomEmpty;
    @BindView(R.id.tvDescription)
    TextView tvDescription;

    // list comment of room
    @BindView(R.id.tvCommentTitle)
    TextView tvCommentTitle;
    @BindView(R.id.rvListComment)
    RecyclerView rvListComment;
    CommentAdapter mCommentAdapter;
    List<Comment> mListComment;

    // layout Your Rating
    ConstraintLayout ratingLayout;
    @BindView(R.id.yourRating)
    SimpleRatingBar yourRating;
    @BindView(R.id.edComment)
    EditText edComment;
    @BindView(R.id.btnRating)
    Button btnRating;

    //layout bottom
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvSeen)
    TextView tvSeen;
    @BindView(R.id.ivCall)
    CircleImageView ivCall;
    @BindView(R.id.ivMessage)
    CircleImageView ivMessage;

    //presenter
    RoomDetailPresenter mPresenter;
    private Room room;

    //get All image
    HashMap<String, String> hashMapForURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);


        ButterKnife.bind(this);

        addControls();
    }

    private void addControls() {
        mPresenter = new RoomDetailPresenter(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // List Comment
        mListComment = new ArrayList<>();
        mCommentAdapter = new CommentAdapter(this, mListComment);
        rvListComment.setAdapter(mCommentAdapter);
        rvListComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        // load room info
        room = mPresenter.getRoomFromBundle(getIntent());
        mPresenter.loadData(room);

        // get comment
        if (!TextUtils.isEmpty(room.getId())){
            mPresenter.getAllComment(room.getId());
        }

        //get image
        hashMapForURL = new HashMap<>();
        mPresenter.getAllImage(room.getId());
    }

    @OnClick({R.id.yourRating, R.id.btnRating})
    void onClick(View view){
        switch (view.getId()){
            case R.id.yourRating:
                mPresenter.putDataRating(room.getId(), yourRating.getRating());
                break;
            case R.id.btnRating:
                if (!TextUtils.isEmpty(edComment.getText().toString().trim())){
                    mPresenter.putDataComment(edComment.getText().toString().trim(), room.getId());
                }
                break;
        }
    }

    @Override
    public void showListComment(List<Comment> listComment) {
        if (listComment != null){
            if (listComment.size() != 0){
                mListComment.clear();
                mListComment.addAll(listComment);
            }else{
                tvCommentTitle.setText("Chưa có bình luận nào");
            }

        }else{
            mListComment = null;
        }
        mCommentAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRoom(Room room) {

        showLoading("Đang tải dữ liệu");
        rating.setRating(Float.valueOf(room.getRating()));
        rating.setIndicator(true);

        tvAddress.setText(room.getAddress()+", "+room.getWard()+", "+room.getDistrict());
        tvArea.setText(room.getArea() + "m2");
        tvRoomEmpty.setText("Số phòng trống: " + room.getRoom_empty());

        tvDescription.setText(room.getDescription());


        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvPrice.setText(formatter.format(room.getPrice())+" VNĐ/phòng/tháng");
        tvSeen.setText(room.getSeen() + " người đã xem phòng này");
        hideLoading();
    }


    // list slide image
    @Override
    public void showImage(List<String> listImage) {
        for (String imageUrl : listImage){
            hashMapForURL.put(imageUrl, imageUrl);
        }
        for (String name : hashMapForURL.keySet()){
            TextSliderView textSliderView =new TextSliderView(this);
            textSliderView.image(hashMapForURL.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());

            textSliderView.getBundle()
                    .putString("extra",name);

            slider.addSlider(textSliderView);
            slider.setPresetTransformer(SliderLayout.Transformer.DepthPage);

            slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

            slider.setCustomAnimation(new DescriptionAnimation());

            slider.setDuration(3000);

            slider.addOnPageChangeListener(this);
        }
    }

    @Override
    public void doneUpdateRating(double rating) {

    }

    @Override
    protected void onStop() {
        slider.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {}
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public void onPageSelected(int position) {}
    @Override
    public void onPageScrollStateChanged(int state) {}
}