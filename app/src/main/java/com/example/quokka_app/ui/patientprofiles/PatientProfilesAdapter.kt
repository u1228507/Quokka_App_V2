import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quokka_app.R
import com.example.quokka_app.ui.patientprofiles.PatientProfilesDataClass
import com.squareup.picasso.Picasso

class PatientProfilesAdapter(
    private val patientprofilelist: ArrayList<PatientProfilesDataClass>,
    private val onItemClick: (PatientProfilesDataClass) -> Unit
) : RecyclerView.Adapter<PatientProfilesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_patientprofiles_cardview,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val patientProfile: PatientProfilesDataClass = patientprofilelist[position]
        val item = patientprofilelist[position]
        holder.firstName.text = patientProfile.firstname
        holder.lastName.text = patientProfile.lastname
        holder.dateOfBirth.text = patientProfile.dateofbirth.toString()

        val imageUrl = item.imageUrl.toString() // Convert the integer to a String

        // Load the image using Picasso
        Picasso.get().load(imageUrl).error(R.drawable.baseline_person_24_purple)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return patientprofilelist.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.first_Name)
        val lastName: TextView = itemView.findViewById(R.id.last_Name)
        val dateOfBirth: TextView = itemView.findViewById(R.id.date_Of_Birth)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(patientprofilelist[position])
                }
            }
        }
    }
}
