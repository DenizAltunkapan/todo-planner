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

<style scoped>
.title {
  text-align: center;
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: #ffffff;
}

.assignee-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  justify-items: center;
  padding: 0 5%;
}

.assignee-card {
  background-color: #2e2e2e;
  border-radius: 10px;
  padding: 2vh 2vw;
  width: 100%;
  max-width: 300px;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  text-align: center;
  transition:
    transform 0.2s ease-in-out,
    box-shadow 0.2s ease-in-out;
}

.assignee-card:hover {
  transform: translateY(-0.3rem);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.4);
}

.assignee-name {
  font-size: clamp(1.2rem, 2.5vw, 1.4rem);
  font-weight: 700;
  margin-bottom: 1vh;
  color: #f1f1f1;
  letter-spacing: 0.5px;
}

.assignee-email {
  font-size: clamp(0.9rem, 2vw, 1rem);
  color: #bbb;
  margin-bottom: 1rem;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

.assignee-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1rem;
}

.delete-btn,
.details-btn {
  background-color: #e63946;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 0.8rem 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
  font-size: clamp(0.9rem, 2vw, 1rem);
}

.delete-btn:hover {
  background-color: #c72c3e;
  transform: scale(1.05);
}

.details-btn {
  background-color: #007bff;
}

.details-btn:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}
</style>
