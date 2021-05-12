package com.example.coconut.ui.main.account.info

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.coconut.Constant
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.databinding.ActivityAccountInfoBinding
import com.example.coconut.model.request.account.AccountEditRequest
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.util.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_account_info.*
import kotlinx.android.synthetic.main.custom_dialog_default.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer
import com.example.coconut.ui.ZoomableImageActivity
import com.example.coconut.ui.main.chat.inner.InnerChatActivity

class AccountInfoActivity : BaseKotlinActivity<ActivityAccountInfoBinding, AccountInfoViewModel>() {

    private val TAG = "AccountInfoActivity"
    private val USER_ID = 0
    private val USER_NAME = 1
    private val USER_DESC = 2

    override val layoutResourceId: Int
        get() = R.layout.activity_account_info
    override var toolbar: Toolbar? = null

    override val viewModel: AccountInfoViewModel by viewModel()
    private val pref: MyPreference by inject()
    private var progressDialog: Dialog? = null

    private val myIdPref = pref.userIdx!!
    private var backImage: String? = null
    private var profileImage: String? = null
    private var Id: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var userNameForIntent: String? = null
    private var userMsg: String? = null

    override fun initStartView() {
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this

        // AccountRecyclerAdapter 에서 받은 intent 정보를 view에 뿌린다
        initViews()
    }

    override fun initDataBinding() {
        viewModel.baseResponseLiveData.observe(this, Observer {
            when (it.success) {
                true -> {
                    normalMode()
                }
                false -> {
                    showToast(it.message)
                }
            }
            progressDialog!!.dismiss()
        })
    }

    override fun initAfterBinding() {
        edit.setOnClickListener { editMode() }
        edit_complete_text.setOnClickListener {
            myIdPref.let {
                val userIdString =
                    Log.e(TAG, "$it / $userId / $profileImage / $backImage")
                viewModel.edit(
                    AccountEditRequest(
                        createPartFromString(it),
                        createPartFromString(userId),
                        createPartFromString(userName),
                        createPartFromString(userMsg),
                        prepareFilePart("profileImage", profileImage),
                        prepareFilePart("backImage", backImage)
                    )
                )
            }
            progressDialog = Dialog(this@AccountInfoActivity).apply {
                setContentView(R.layout.custom_loading_dialog)
                setCancelable(true)
                show()
            }
        }
        user_id_edit_icon.setOnClickListener { editUserData(USER_ID) }
        user_name_edit_icon.setOnClickListener { editUserData(USER_NAME) }
        user_desc_edit_icon.setOnClickListener { editUserData(USER_DESC) }
        user_img_edit_icon.setOnClickListener { selectImage(IntentID.PROFILE_IMAGE) }

        back_img_edit_icon.setOnClickListener {
            when ((it as ImageView).drawable.constantState) {
                resources.getDrawable(R.drawable.x, null).constantState -> {
                    this@AccountInfoActivity.finish()
                }
                resources.getDrawable(R.drawable.ic_edit_location_black_24dp, null).constantState -> {
                    selectImage(IntentID.BACKGROUND_IMAGE)
                }
            }
        }

        user_image.setOnClickListener {
            intent.getParcelableExtra<UserDataResponse>(IntentID.USER_RESPONSE)?.let {
                Intent(this@AccountInfoActivity, ZoomableImageActivity::class.java).run {
                    putExtra(IntentID.USER_IMAGE, it.profilePicture)
                    startActivity(this)
                }
            }
        }
        background_image.setOnClickListener {
            intent.getParcelableExtra<UserDataResponse>(IntentID.USER_RESPONSE)?.let {
                Intent(this@AccountInfoActivity, ZoomableImageActivity::class.java).run {
                    putExtra(IntentID.USER_IMAGE, it.backgroundPicture)
                    startActivity(this)
                }
            }
        }

        chat.setOnClickListener { callActivity(Constant.CHAT_PAGE) }
        call.setOnClickListener {
            showToast("준비중입니다")
//            callActivity(Constant.CALL_PAGE)
        }
    }

    override fun onBackPressed() {
        when (edit_complete_text.visibility) {
            View.VISIBLE -> {
                Dialog(this@AccountInfoActivity).apply {
                    setContentView(R.layout.custom_dialog_default)
                    show()

                    dialog_title.gone()
                    dialog_edit_textinput.gone()
                    dialog_content.text = getString(R.string.edit_cancel_alert)
                    dialog_positive.setOnClickListener { normalMode(); dismiss() }
                    dialog_negative.setOnClickListener { dismiss() }
                }
            }
            View.INVISIBLE -> {
                super.onBackPressed()
            }
            View.GONE -> {
            }
        }
    }

    private fun initViews() {
        intent.getParcelableExtra<UserDataResponse>(IntentID.USER_RESPONSE)?.let {
            Id = it.id
            user_name.text = it.name
            userNameForIntent = it.name
            user_id_.text = it.userId
            user_msg.text = it.stateMessage

            //picasso 는 path 변수가 null 이면 오류발생하므로 null-safe걸어준다
            it.profilePicture?.run {
                Glide.with(this@AccountInfoActivity)
                    .load(this.toHTTPString())
                    .placeholder(R.drawable.account)
                    .into(user_image)
            }
            it.backgroundPicture?.run {
                Glide.with(this@AccountInfoActivity)
                    .load(this.toHTTPString())
                    .placeholder(R.drawable.black)
                    .into(background_image)
            }

            //해당 프로필이 자신의 프로필이면
            //edit버튼 보이기
            myIdPref.let { id ->
                if (id == it.id) {
                    edit.show()
                    chat_text_view.text = getString(R.string.chatting_to_myself)
                }
            }
        }

        normalMode()
    }

    private fun normalMode() {
        user_img_edit_icon.hide()
        user_id_edit_icon.hide()
        user_name_edit_icon.hide()
        user_desc_edit_icon.hide()
        edit_complete_text.hide()
        select_space.show()

        back_img_edit_icon.setImageResource(R.drawable.x)

        if (user_msg.text.toString().isBlank())
            user_msg.hide()
        if (user_id_.text.toString().isBlank())
            user_id_.hide()

        user_image.isClickable = true
        background_image.isClickable = true

        backImage = null
        profileImage = null
        userId = null
        userName = null
    }

    private fun editMode() {
        user_img_edit_icon.show()
        user_id_edit_icon.show()
        user_name_edit_icon.show()
        user_desc_edit_icon.show()
        edit_complete_text.show()
        user_msg.show()
        select_space.hide()

        back_img_edit_icon.setImageResource(R.drawable.ic_edit_location_black_24dp)

        user_image.isClickable = false
        background_image.isClickable = false
    }

    private fun editUserData(which: Int) {
        val idArray = arrayListOf(
            R.id.user_id_, R.id.user_name, R.id.user_msg
        )
        val titleArray = arrayListOf(
            getString(R.string.changing_user_id),
            getString(R.string.changing_user_name),
            getString(R.string.changing_user_desc)
        )

        val textView = findViewById<TextView>(idArray[which])
        Dialog(this@AccountInfoActivity).apply {

            setContentView(R.layout.custom_dialog_default)
            setCancelable(false)
            show()

            dialog_title.text = titleArray[which]
            dialog_edit_text.text = textView.text.toString().toEditable()
            dialog_content.gone()

            dialog_positive.setOnClickListener {
                dialog_edit_text.text.toString().let { text ->
                    Log.e(TAG, text)
                    when (which) {
                        USER_ID -> {
                            if (text.length > 20 || text.length < 2 || text.contains(" ")) {
                                showToast("아이디는 2 ~ 20 자 (공백 불가)")
                                return@let
                            }
                            userId = text
                        }
                        USER_NAME -> {
                            if (text.length > 10 || text.length < 2 || text.contains(" ")) {
                                showToast("이름는 2 ~ 10 자 (공백 불가)")
                                return@let
                            }
                            userName = text
                        }
                        USER_DESC -> {
                            if (text.length > 30) {
                                showToast("상태메시지는 30자 이내")
                                return@let
                            }
                            userMsg = text
                        }
                    }
                    textView.text = text
                    this.dismiss()
                }
            }
            dialog_negative.setOnClickListener { dismiss() }
        }
    }

    private fun callActivity(where: Int) {
        when (where) {
            Constant.CHAT_PAGE -> {
                Id?.let {
                    val intent = Intent(applicationContext, InnerChatActivity::class.java)
                    when (myIdPref == it) {
                        true -> {
                            intent.putExtra(IntentID.CHAT_MODE, IntentID.CHAT_WITH_ME)
                            intent.putExtra(IntentID.CHAT_ROOM_TITLE, userNameForIntent)
                        }
                        false -> {
                            intent.putExtra(IntentID.CHAT_MODE, IntentID.CHAT_WITH_ONE_PARTNER)
                            intent.putExtra(IntentID.CHAT_ROOM_TITLE, userNameForIntent)
                            intent.putExtra(IntentID.ID, it)
                        }
                    }
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    startActivity(intent)
                    finish()
                }
            }
            Constant.CALL_PAGE -> {
                Id?.let {
                    val intent = Intent(applicationContext, InnerChatActivity::class.java)
                    intent.putExtra(IntentID.ID, it)
                    startActivity(intent)
                }
            }
        }
    }

    private fun selectImage(to: Int) {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Log.e("cameraPermission", "Permission Granted")

                // 카메라 , 앨범 퍼미션 허가시
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    //type = MediaStore.Images.Media.CONTENT_TYPE
                    type = "image/*"
                    //data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    startActivityForResult(this, to)
                }
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Log.e("cameraPermission", "Permission Denied : ${deniedPermissions.toString()}")
            }
        }

        TedPermission.with(applicationContext)
            .setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK)
            return

        when (requestCode) {
            IntentID.BACKGROUND_IMAGE -> {
                data!!.data?.let { selectedImageUri ->
                    try {
                        background_image.setImageURI(selectedImageUri)
                        backImage = getPath(selectedImageUri)
                        Log.e(TAG, "backImage : $backImage")

                    } catch (e: Exception) {
                        showToast("앨범에서 사진 가져오기 에러")
                        Log.e(TAG, "앨범에서 사진 가져오기 에러 : ${e.message}")
                    }
                }
            }

            IntentID.PROFILE_IMAGE -> {
                data!!.data?.let { selectedImageUri ->
                    try {
                        user_image.setImageURI(selectedImageUri)
                        profileImage = getPath(selectedImageUri)
                        Log.e(TAG, "profileImage : $profileImage")

                    } catch (e: Exception) {
                        showToast("앨범에서 사진 가져오기 에러")
                        Log.e(TAG, "앨범에서 사진 가져오기 에러 : ${e.message}")
                    }
                }
            }
        }
    }

}
