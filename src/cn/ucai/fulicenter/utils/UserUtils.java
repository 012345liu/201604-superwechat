package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.domain.User;

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
            Picasso.with(context).load(user.getAvatar()).placeholder(cn.ucai.fulicenter.R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(cn.ucai.fulicenter.R.drawable.default_avatar).into(imageView);
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
			Picasso.with(context).load(path).placeholder(cn.ucai.fulicenter.R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
//    /**
//     * 设置群组头像
//     * @param hxid
//     */
//    public static void setAppGroupAvatar(Context context, String hxid, ImageView imageView){
//		String path = "";
//        if(path != null && hxid!= null){
//			path = getGroupAvatarPath(hxid);
//			Log.e(TAG,"path="+path);
//			Picasso.with(context).load(path).placeholder(R.drawable.group_icon).into(imageView);
//        }else{
//            Picasso.with(context).load(R.drawable.group_icon).into(imageView);
//        }
//    }


	public static String getUserAvatarPath(String username) {
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
			Picasso.with(context).load(user.getAvatar()).placeholder(cn.ucai.fulicenter.R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(cn.ucai.fulicenter.R.drawable.default_avatar).into(imageView);
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
		UserAvatar user = FuLiCenterApplication.getInstance().getUserMap().get(username);
		if(user == null){
			user = new UserAvatar();
		}
		return user;

	}
//	public static MemberUserAvatar getAppMemberInfo(String hxid,String username) {
//		MemberUserAvatar member=null;
//		HashMap<String, MemberUserAvatar> members =
//				FuLiCenterApplication.getInstance().getMemberMap().get(hxid);
//		if (members == null || members.size() < 0) {
//			return null;
//		} else {
//			member = members.get(username);
//		}
//		return member;
//
//	}

	public static void setAppCurrentUserNick(Context context,TextView textView) {
		UserAvatar user = FuLiCenterApplication.getInstance().getUser();
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
		String userName = FuLiCenterApplication.getInstance().getUserName();
		setAppUserAvatar(context, userName, imageView);

	}

//	public static void setAppMemberNick(String hxid, String username, TextView textView) {
//		MemberUserAvatar member = getAppMemberInfo(hxid, username);
//		if (member != null && username != null) {
//			textView.setText(member.getMUserNick());
//		} else {
//			textView.setText(username);
//		}
//	}
}
