package cn.ucai.superwechat.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.domain.User;

public class UserUtils {
	private static final String TAG = UserUtils.class.getSimpleName();

	/**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new User(username);
        }

        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }

    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
        }
    }
    /**
     * 设置用户头像
     * @param username
     */
    public static void setAppUserAvatar(Context context, String username, ImageView imageView){
		String path = "";
        if(path != null && username!= null){
			path = getUserAvatarPath(username);
			Log.e(TAG,"path="+path);
			Picasso.with(context).load(path).placeholder(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

	private static String getUserAvatarPath(String username) {
		StringBuilder path = new StringBuilder(I.SERVER_ROOT);
		path.append(I.QUESTION).append(I.KEY_REQUEST)
				.append(I.EQU).append(I.REQUEST_DOWNLOAD_AVATAR)
				.append(I.AND)
				.append(I.NAME_OR_HXID).append(I.EQU).append(username)
				.append(I.AND)
				.append(I.AVATAR_TYPE).append(I.EQU)
				.append(I.AVATAR_TYPE_USER_PATH);
		return path.toString();

	}

	/**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
		}
	}

    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }

    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(user.getNick());
    	}
    }

    /**
     * 保存或更新某个用户
     * @param newUser
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}



	/**
	 * 设置用户好友的昵称
	 */
	public static void setAppUserNick(String username, TextView textview) {
		UserAvatar user = getAppUserInfo(username);
		setAppUserNick(user,textview);

	}

	/**
	 * 设置用户好友的昵称
	 */
	public static void setAppUserNick(UserAvatar user , TextView textview) {
		if (user!=null) {
			if(user.getMUserNick() != null){
				textview.setText(user.getMUserNick());

			}else{
				textview.setText(user.getMUserName());
			}
		}else{
			textview.setText(user.getMUserName());
		}

	}



	/**
	 * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
	 * @param username
	 * @return
	 */
	public static UserAvatar getAppUserInfo(String username) {
		UserAvatar user = SuperWeChatApplication.getInstance().getUserMap().get(username);
		if(user == null){
			user = new UserAvatar();
		}
		return user;

	}

	public static void setAppCurrentUserNick(Context context,TextView textView) {
		UserAvatar user = SuperWeChatApplication.getInstance().getUser();
		if (user != null && textView!= null) {
			if (user.getMUserNick()!=null) {
				textView.setText(user.getMUserNick());
			}
		} else {
			textView.setText(user.getMUserName());
		}
	}

	/**
	 * 设置当前用户头像
	 * @param context
	 * @param imageView
     */

	public static void setAppCurrentUserAvatar(Context context, ImageView imageView) {
		String userName = SuperWeChatApplication.getInstance().getUserName();
		setAppUserAvatar(context, userName, imageView);

	}
}
