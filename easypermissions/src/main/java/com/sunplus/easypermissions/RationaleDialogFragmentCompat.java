package com.sunplus.easypermissions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * Created by w.feng on 2018/10/10
 * Email: fengweisb@gmail.com
 */
public class RationaleDialogFragmentCompat extends AppCompatDialogFragment {
  public static final String TAG = "RationaleDialogFragmentCompat";

  private EasyPermissions.PermissionCallbacks mPermissionCallbacks;
  private EasyPermissions.RationaleCallbacks mRationaleCallbacks;

  public static RationaleDialogFragmentCompat newInstance(
      @NonNull String rationaleMsg,
      @NonNull String positiveButton,
      @NonNull String negativeButton,
      @StyleRes int theme,
      int requestCode,
      @NonNull String[] permissions) {

    // Create new Fragment
    RationaleDialogFragmentCompat dialogFragment = new RationaleDialogFragmentCompat();

    // Initialize configuration as arguments
    RationaleDialogConfig config = new RationaleDialogConfig(
        positiveButton, negativeButton, rationaleMsg, theme, requestCode, permissions);
    dialogFragment.setArguments(config.toBundle());

    return dialogFragment;
  }


  public void showAllowingStateLoss(FragmentManager manager, String tag) {
    if (manager.isStateSaved()) {
      return;
    }

    show(manager, tag);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (getParentFragment() != null) {
      if (getParentFragment() instanceof EasyPermissions.PermissionCallbacks) {
        mPermissionCallbacks = (EasyPermissions.PermissionCallbacks) getParentFragment();
      }
      if (getParentFragment() instanceof EasyPermissions.RationaleCallbacks) {
        mRationaleCallbacks = (EasyPermissions.RationaleCallbacks) getParentFragment();
      }
    }

    if (context instanceof EasyPermissions.PermissionCallbacks) {
      mPermissionCallbacks = (EasyPermissions.PermissionCallbacks) context;
    }

    if (context instanceof EasyPermissions.RationaleCallbacks) {
      mRationaleCallbacks = (EasyPermissions.RationaleCallbacks) context;
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mPermissionCallbacks = null;
    mRationaleCallbacks = null;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Rationale dialog should not be cancelable
    setCancelable(false);

    // Get config from arguments, create click listener
    RationaleDialogConfig config = new RationaleDialogConfig(getArguments());
    RationaleDialogClickListener clickListener =
        new RationaleDialogClickListener(this, config, mPermissionCallbacks, mRationaleCallbacks);

    // Create an AlertDialog
    return config.createSupportDialog(getContext(), clickListener);
  }
}