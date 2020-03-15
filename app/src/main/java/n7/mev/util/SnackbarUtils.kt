/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package n7.mev.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Provides a method to show a Snackbar.
 */
object SnackbarUtils {
    fun showSnackbar(v: View?, snackbarText: String?) {
        if (v == null || snackbarText == null) {
            return
        }
        //        Snackbar.make(v, snackbarText, Snackbar.LENGTH_LONG).setAnchorView(viewAbove).show();
        Snackbar.make(v, snackbarText, Snackbar.LENGTH_LONG).show()
    }

    fun showSnackbarWithAction(v: View?, snackbarText: String?, actionText: String?, actionClick: View.OnClickListener?) {
        if (v == null || snackbarText == null) {
            return
        }
        val snackbar = Snackbar.make(v, snackbarText, Snackbar.LENGTH_LONG).setAction(actionText, actionClick)
        snackbar.show()
    }
}