package com.example.sns_project


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.sns_project.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class homeFragment : Fragment(R.layout.fragment_home) {

    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter : PostAdapter? = null
    private val userinfoCollectionRef = db.collection("userinfo")
    private val userPostCollectionRef = db.collection("userPost")
    private var snapshotListener: ListenerRegistration?= null
    var postMailList: ArrayList<String> = arrayListOf()

    private lateinit var binding: FragmentHomeBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val binding = FragmentHomeBinding.bind(view)
        binding = FragmentHomeBinding.bind(view)

        binding.buttonPost.setOnClickListener {
            startActivity(Intent(activity,PostActivity::class.java)) //버튼클릭 시 포스트로 이동
        }
        //DB에서 가져와서 리사이클러뷰에 표시

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        //binding.recyclerView.adapter = PostAdapter
        adapter = context?.let { PostAdapter(it, emptyList()) }
        binding.recyclerView.adapter =adapter

        updateList()
    }

    private fun updateList(){
        var userArr: ArrayList<userDTO> = arrayListOf() // 유저의 이메일(id값)을 담을 배열
        var userList: ArrayList<String> = arrayListOf() // 유저의 이메일(id값)을 담을 배열
        userinfoCollectionRef.get().addOnSuccessListener {
            for (doc in it) {

                var item = doc.toObject(userDTO::class.java)
                if (item != null) {
                    if(item.show=="none"){
                        userArr.add(item!!)
                        userList.add(doc.id)
                    }

                } // 이름 ( doc.name)



            }

        }
        userPostCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener {
            val items = mutableListOf<Items>()
            for(doc in it){
                var count = 0
                for(i in 0 until userList.size!!){
                    if(Items(doc).userMail == userList[i]){
                        count = 1
                    }
                }
                if(count != 1){
                    items.add(Items(doc))
                }

            }
            adapter?.updateList(items)
        }
    }



}