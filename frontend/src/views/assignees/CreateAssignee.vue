<template>
  <h1>Create Assignee</h1>

  <form @submit.prevent="createAssignee" class="form-container">
    <div class="input-group">
      <label for="prename">Prename:</label>
      <input
        id="prename"
        v-model="assignee.prename"
        type="text"
        placeholder="Enter prename"
        required
      />
    </div>
    <div class="input-group">
      <label for="name">Name:</label>
      <input id="name" v-model="assignee.name" type="text" placeholder="Enter name" required />
    </div>
    <div class="input-group">
      <label for="email">Email:</label>
      <input id="email" v-model="assignee.email" type="email" placeholder="Enter email" required />
    </div>

    <button type="submit" class="submit-btn">Create Assignee</button>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { showToast, Toast } from '@/ts/toasts'
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons'
import config from '@/config'
import { useRouter } from 'vue-router'
import '/src/assets/form.css'

const router = useRouter()

const assignee = ref({
  prename: '',
  name: '',
  email: ''
})

function createAssignee() {
  if (!assignee.value.prename || !assignee.value.name || !assignee.value.email) {
    showToast(new Toast('Validation Error', 'All fields are required.', 'error', faXmark, 5))
    return
  }

  fetch(`${config.apiBaseUrl}/assignees`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(assignee.value)
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(
          'Failed to create assignee. Check that the email ends with uni-stuttgart.de and no field is empty.'
        )
      }
      return response.json()
    })
    .then(() => {
      showToast(new Toast('Success', 'Assignee successfully created!', 'success', faCheck, 5))
      assignee.value = { prename: '', name: '', email: '' }
      setTimeout(() => {
        router.push('/assignees')
      }, 300)
    })
    .catch((error) => {
      showToast(new Toast('Error', error.message, 'error', faXmark, 5))
    })
}
</script>
