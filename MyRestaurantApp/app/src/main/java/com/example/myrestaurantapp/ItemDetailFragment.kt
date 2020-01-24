package com.example.myrestaurantapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myrestaurantapp.dummy.DummyContent
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.*

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: DummyContent.DummyItem? = null
    private var res_id_txt :String?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
                res_id_txt=it.getString(ARG_ITEM_ID)!!.toString()

            }
        }

    }
    fun getRestaurant( res_id:String?) {
        // Instantiate the RequestQueue.
        val url: String = "https://developers.zomato.com/api/v2.1/restaurant?res_id="+res_id


        // new Volley newRequestQueue
        val updateQueue = Volley.newRequestQueue(activity)

        val updateReq = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                var jso = it
               // DummyContent.ITEMS.clear()
              ///  for (i in 0 until jso.length()){

                    //var t = jso.getJSONObject(i).getJSONObject("restaurant")
                    var location =jso.getJSONObject("location").getString("address")

                    var name = jso.getString("name")
                activity?.toolbar_layout?.title = name.toString()

               // item_detail.setText(name.toString())
                var cuisines = jso.getString("cuisines")
                var timmimgs =jso.getString("timings")
                timings.setText(timmimgs.toString())
                resadd!!.setText(location!!.toString())
                var userratings = jso.getJSONObject("user_rating").getInt("aggregate_rating")
                ratingbar.rating=userratings.toFloat()
                cuisine.setText(cuisines.toString())
                var  photo =jso.getString("thumb")

                var phonenumber = jso.getString(  "phone_numbers" )
                contactnumbers.setText("Call "+phonenumber.toString()+" for reservation & enquiry")
                if(photo.toString().length>1) {
                    Picasso.get()
                        //.load(jso.getJSONArray("photos").getJSONObject(0).getString("url"))
                        .load(photo)

                        .into(resiv, object : Callback {
                            override fun onSuccess() {
                                //   Log.d(TAG, "success")
                            }

                            override fun onError(e: Exception?) {
                                //  Log.d(TAG, "error")
                            }
                        })
                }


                //   }
                //item_list.adapter!!.notifyDataSetChanged()
                //Toast.makeText(appli,"Ok", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
              println(it.toString())
            }) {

            // override getHeader for pass session to service
            override fun getHeaders(): MutableMap<String, String> {

                val header = mutableMapOf<String, String>()
                // "Cookie" and "PHPSESSID=" + <session value> are default format
                header.put("user-key", "08c9243059c11a9938ee6ff39820fb72" )
              //  header.put("res_id", res_id!!.toString() )
                return header
            }
        }
        updateQueue.add(updateReq)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.item_detail.text = it.details
        }
        getRestaurant(res_id_txt)
        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
