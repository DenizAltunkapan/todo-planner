<template>
  <h1 class="title">Assignees</h1>

  <div v-if="assignees.length === 0">
    <p>No assignee available on the server...</p>
    <p>
      <a href="#" @click.prevent="navigateToCreateAssignee">Click here</a> to create an assignee.
    </p>
  </div>

  <div v-else class="assignee-container">
    <div class="assignee-card" v-for="assignee in assignees" :key="assignee.id">
      <h3 class="assignee-name">{{ assignee.name }}, {{ assignee.prename }}</h3>
      <p class="assignee-email">Email: {{ assignee.email }}</p>
      <div class="assignee-actions">
        <Button @click="deleteAssignee(assignee.id)" mode="secondary" class="delete-btn">
          <FontAwesomeIcon :icon="faTrash" class="icon" />
          Delete
        </Button>
        <Button @click="navigateToDetails(assignee.id)" mode="primary" class="details-btn">
          <FontAwesomeIcon :icon="faEdit" class="icon" />
          Details
        </Button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Button } from 'agnostic-vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import config from '@/config'
import { showToast, Toast } from '@/ts/toasts'
import { faCheck, faXmark, faTrash, faEdit } from '@fortawesome/free-solid-svg-icons'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import '/src/assets/styling/assignees-view.css'

interface Assignee {
  id: number
  name: string
  prename: string
  email: string
}

const assignees = ref<Assignee[]>([])
const router = useRouter()

function fetchAllAssignees() {
  fetch(`${config.apiBaseUrl}/assignees`)
    .then((response) => response.json())
    .then((data) => (assignees.value = data))
    .catch((error) => showToast(new Toast('Error', error, 'error', faXmark, 10)))
}

function deleteAssignee(id: number) {
  const confirmDelete = window.confirm('Are you sure you want to delete this assignee?')
  if (confirmDelete) {
    fetch(`${config.apiBaseUrl}/assignees/${id}`, { method: 'DELETE' })
      .then(() => {
        assignees.value = assignees.value.filter((assignee) => assignee.id !== id)
        showToast(
          new Toast('Alert', `Successfully deleted assignee with ID ${id}!`, 'success', faCheck, 5)
        )
      })
      .catch((error) => showToast(new Toast('Error', error, 'error', faXmark, 10)))
  }
}

function navigateToDetails(id: number) {
  router.push(`/assignees/${id}`)
}

onMounted(() => fetchAllAssignees())

function navigateToCreateAssignee() {
  router.push('/create-assignee')
}
</script>
