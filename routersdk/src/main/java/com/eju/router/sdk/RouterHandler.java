package com.eju.router.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.eju.router.sdk.exception.EjuException;

/**
 * handle route action
 *
 * @author SidneyXu
 */
/* package */ class RouterHandler {

    /**
     * start activity
     *
     * @param context from
     * @param className to
     * @param paramAdapter param
     * @throws EjuException if error
     */
    public void startActivity(Context context, String className, ParamAdapter paramAdapter) throws EjuException {
        Intent intent = intent(context, className, paramAdapter);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * start activity from fragment
     *
     * @param fragmentAdapter fragment adapter
     * @param className to
     * @param paramAdapter param
     * @throws EjuException if error
     */
    public void startActivityFromFragment(FragmentAdapter fragmentAdapter, String className, ParamAdapter paramAdapter) throws EjuException {
        Intent intent = intent(fragmentAdapter.getActivity(), className, paramAdapter);
        fragmentAdapter.startActivity(intent);
    }

    /**
     * start activity for result
     * @param context from
     * @param className to
     * @param paramAdapter param
     * @param requestCode request code
     * @throws EjuException if error
     */
    public void startActivityForResult(Context context, String className, ParamAdapter paramAdapter, int requestCode) throws EjuException {
        Intent intent = intent(context, className, paramAdapter);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, requestCode);
        } else {
            throw new EjuException(EjuException.ILLEGAL_PARAMETER, "mContext should be an instance of Activity");
        }
    }

    /**
     * start activity for result from fragment
     *
     * @param fragmentAdapter fragment adapter
     * @param className to
     * @param paramAdapter param
     * @param requestCode request code
     * @throws EjuException if error
     */
    public void startActivityForResultFromFragment(FragmentAdapter fragmentAdapter, String className, ParamAdapter paramAdapter, int requestCode) throws EjuException {
        Intent intent = intent(fragmentAdapter.getActivity(), className, paramAdapter);
        fragmentAdapter.startActivityForResult(intent, requestCode);
    }

    private Intent intent(Context context, String className, ParamAdapter paramAdapter) throws EjuException {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new EjuException(EjuException.RESOURCE_NOT_FOUND, "Resource '" + className + "'not found!");
        }
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        if (null != paramAdapter) {
            intent.putExtras(paramAdapter.toBundle());
        }
        return intent;
    }
}
