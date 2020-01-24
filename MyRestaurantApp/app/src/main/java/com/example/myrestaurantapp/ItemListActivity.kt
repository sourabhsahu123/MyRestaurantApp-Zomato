package com.example.myrestaurantapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.example.myrestaurantapp.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*
import kotlinx.android.synthetic.main.item_list.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }



        getRestaurant("New York")
        setupRecyclerView(item_list)
        searchbtn.setOnClickListener {
            getRestaurant(cityname.text.toString())
        }
    }
    // function for network call
    fun getRestaurant( restaurantname:String?) {
        // Instantiate the RequestQueue.
         val url: String = "https://developers.zomato.com/api/v2.1/search?q="+restaurantname


        // new Volley newRequestQueue
        val updateQueue = Volley.newRequestQueue(this)

        val updateReq = object : JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener {var jso = it.getJSONArray("restaurants")
                DummyContent.ITEMS.clear()
              for (i in 0 until jso.length()){

                  var t = jso.getJSONObject(i).getJSONObject("restaurant")
                  var id =t.getString("id")
                  var name = t.getString("name")
                  var add = t.getJSONObject("location").getString("address")

                  var x =DummyContent.DummyItem(id,name,add)
                  DummyContent.ITEMS.add(x)


              }
              item_list.adapter!!.notifyDataSetChanged()
                Toast.makeText(this,"Ok", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
Toast.makeText(this,"Error: "+it.toString(),Toast.LENGTH_LONG).show()
            }) {

            // override getHeader for pass session to service
            override fun getHeaders(): MutableMap<String, String> {

                val header = mutableMapOf<String, String>()
                // "Cookie" and "PHPSESSID=" + <session value> are default format
                header.put("user-key", "08c9243059c11a9938ee6ff39820fb72" )
                return header
            }
        }
        updateQueue.add(updateReq)


    }
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ItemListActivity,
        private val values: List<DummyContent.DummyItem>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.id
            holder.contentView.text = item.content
            holder.add.text =item.details

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
            val add :TextView=view.address
        }
    }
}
