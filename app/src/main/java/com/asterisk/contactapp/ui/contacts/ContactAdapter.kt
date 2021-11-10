package com.asterisk.contactapp.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asterisk.contactapp.R
import com.asterisk.contactapp.data.Contact
import com.asterisk.contactapp.databinding.ItemContactBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ContactAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Contact, ContactAdapter.ContactViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val contact = getItem(position)
                        listener.onItemClicked(contact)
                    }
                }

            }
        }

        fun bind(contact: Contact) {
            binding.apply {
                Glide.with(itemView)
                    .load(contact.image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .error(R.drawable.default_avatar)
                    .into(ivContactImage)
                tvContactName.text = contact.name
                tvContactNumber.text = contact.phone
                ivFavContact.setImageResource(if (contact.favorite) R.drawable.ic_heart_active else R.drawable.ic_heart_inactive)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(contact: Contact)
    }

    class DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact) =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Contact, newItem: Contact) =
            oldItem == newItem
    }
}