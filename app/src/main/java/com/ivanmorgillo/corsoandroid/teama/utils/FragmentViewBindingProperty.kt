package com.ivanmorgillo.corsoandroid.teama.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * This file contains a set of extensions that allow us to simplify the way in which [ViewBinding]s are handled.
 *
 * According to [android official documentation](https://developer.android.com/topic/libraries/view-binding#fragments),
 * using [ViewBinding] in [Fragment]s requires users to explicitly take care of releasing the view
 * when [Fragment.onDestroyView] is called, in order to avoid leaks. This leads to code that looks
 * like this:
 *
 * ```
 * private var _binding: MyFragmentBinding? = null
 * // This property is only valid between onCreateView and
 * // onDestroyView.
 * private val binding get() = _binding!!
 *
 * override fun onCreateView(
 *   inflater: LayoutInflater,
 *   container: ViewGroup?,
 *   savedInstanceState: Bundle?
 * ): View? {
 *   _binding = ResultProfileBinding.inflate(inflater, container, false)
 *   val view = binding.root
 *   return view
 * }
 *
 * override fun onDestroyView() {
 *   super.onDestroyView()
 *   _binding = null
 * }
 * ```
 *
 * This set of utilities allows the simplification of the fragment code by abstracting away the
 * handling of the view lifecycle:
 *
 * ```
 * // This property is only valid between onCreateView and
 * // onDestroyView.
 * private val binding by viewBinding(MyFragmentBinding::bind)
 * ```
 *
 * NOTE: although this extension allows for a cleaner way of declaring and using the viewBinding the
 * binding is still only available in between [Fragment.onCreateView] and [Fragment.onDestroyView].
 * Trying to access the binding outside of this interval will cause an [IllegalStateExcetion] to be
 * thrown.
 */

private class FragmentViewBindingProperty<F : Fragment, T : ViewBinding>(viewBinder: (F) -> T) :
    ViewBindingProperty<F, T>(viewBinder) {
    override fun getLifecycleOwner(thisRef: F) = thisRef.viewLifecycleOwner
}

/**
 * Create new [ViewBinding] associated with the [Fragment]
 */
fun <F : Fragment, T : ViewBinding> Fragment.viewBinding(viewBinder: (F) -> T): ViewBindingProperty<F, T> =
    FragmentViewBindingProperty(viewBinder)

/**
 * Create new [ViewBinding] associated with the [Fragment]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewProvider Provide a [View] from the Fragment. By default call [Fragment.requireView]
 */
@JvmName("viewBindingFragment")
inline fun <F : Fragment, T : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> T,
    crossinline viewProvider: (F) -> View = Fragment::requireView,
): ViewBindingProperty<F, T> = viewBinding { fragment: F -> vbFactory(viewProvider(fragment)) }

/**
 * Create new [ViewBinding] associated with the [Fragment]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 */
@JvmName("viewBindingFragment")
inline fun <T : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> T,
    @IdRes viewBindingRootId: Int,
): ViewBindingProperty<Fragment, T> =
    viewBinding(vbFactory) { fragment: Fragment -> fragment.requireView().findViewById(viewBindingRootId) }
