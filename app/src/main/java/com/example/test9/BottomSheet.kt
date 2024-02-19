package com.example.test9

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.test9.databinding.FragmentBottomSheetBinding
import com.example.test9.presentation.callback.OnTakePhotoListener

class BottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    interface BottomSheetListener {
        fun onOptionSelected(option: String)
    }

    private var listener: BottomSheetListener? = null

    var onTakePhotoListener: OnTakePhotoListener? = null

    fun setListener(listener: BottomSheetListener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.takePhotoBtn.setOnClickListener {
            onTakePhotoListener?.onTakePicture()
            dismiss()
        }
        binding.choosePhotoBtn.setOnClickListener {
            listener?.onOptionSelected("CHOOSE_GALLERY")
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
