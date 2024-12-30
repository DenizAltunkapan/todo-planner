<template>
  <h1 class="title">Edit Assignee</h1>
  <div class="form-container">
    <form @submit.prevent="updateAssignee" class="form">
      <div class="input-group">
        <label for="prename">Prename:</label>
        <input
          id="prename"
          v-model="assignee.prename"
          type="text"
          placeholder="Enter prename"
          required
          class="input-field"
        />
      </div>
      <div class="input-group">
        <label for="name">Name:</label>
        <input
          id="name"
          v-model="assignee.name"
          type="text"
          placeholder="Enter name"
          required
          class="input-field"
        />
      </div>
      <div class="input-group">
        <label for="email">Email:</label>
        <input
          id="email"
          v-model="assignee.email"
          type="email"
          placeholder="Enter email"
          required
          class="input-field"
        />
      </div>

      <div class="button-container">
        <button type="button" @click="goBack" class="go-back-btn">Go Back</button>
        <button type="submit" class="submit-btn">Update Assignee</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, Toast } from '@/ts/toasts'
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons'
import config from '@/config'
import '/src/assets/styling/detail.css'

defineProps({
  /**
   * The id of the assignee to be edited
   * @type {string}
   */
  id: {
    type: String,
    required: true
  }
})

interface Assignee {
  id: string
  prename: string
  name: string
  email: string
}

const route = useRoute()
const router = useRouter()
const assignee = reactive<Assignee>({
  id: '0',
  prename: '',
  name: '',
  email: ''
})

/**
 * Fetches the details of the assignee
 * If fetching fails, an error toast is displayed
 */
function fetchAssignee() {
  const id = route.params.id
  if (!id) {
    showToast(new Toast('Error', 'Invalid Assignee ID.', 'error', faXmark, 5))
    router.push('/assignees')
    return
  }

  fetch(`${config.apiBaseUrl}/assignees/${id}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error('Failed to fetch assignee.')
      }
      return response.json()
    })
    .then((data: Assignee) => {
      Object.assign(assignee, data)
    })
    .catch((error) => {
      showToast(new Toast('Error', error.message, 'error', faXmark, 5))
    })
}

/**
 * Updates the assignee's details by sending a PUT request to the API
 * Displays a success or error toast based on the response
 */
function updateAssignee() {
  fetch(`${config.apiBaseUrl}/assignees/${assignee.id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(assignee)
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(
          'Failed to update assignee. Check that the email ends with uni-stuttgart.de and no field is empty.'
        )
      }
      showToast(new Toast('Success', 'Assignee successfully updated!', 'success', faCheck, 5))
    })
    .catch((error) => {
      showToast(new Toast('Error', error.message, 'error', faXmark, 5))
    })
}

/**
 * Navigates the user back to the previous page (the assignees overview)
 */
function goBack() {
  router.back()
}

onMounted(fetchAssignee)
</script>
