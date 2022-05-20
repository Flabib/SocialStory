package id.my.fahdilabib.socialstory.data.remote.responses

import com.google.gson.annotations.SerializedName

data class StoryResponse(
	@field:SerializedName("id")
	val id: String,
	@field:SerializedName("name")
	val name: String,
	@field:SerializedName("photoUrl")
	val photoUrl: String,
	@field:SerializedName("description")
	val description: String,
	@field:SerializedName("lon")
	val lon: Double,
	@field:SerializedName("lat")
	val lat: Double,
	@field:SerializedName("createdAt")
	val createdAt: String,
)