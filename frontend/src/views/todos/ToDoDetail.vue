<template>
  <h1 class="title">Edit ToDo</h1>

  <div v-if="todo" class="form-container">
    <form @submit.prevent="updateToDo" class="form">
      <div class="input-group">
        <label for="title">Title:</label>
        <input id="title" v-model="todo.title" class="input-field" required />
      </div>
      <div class="input-group">
        <label for="description">Description:</label>
        <textarea id="description" v-model="todo.description" class="input-field"></textarea>
      </div>
      <div class="input-group">
        <label for="dueDate">Due Date:</label>
        <input id="dueDate" v-model="dueDate" type="date" class="input-field" />
        <label for="dueTime">Due Time:</label>
        <input id="dueTime" v-model="dueTime" type="time" class="input-field" />
      </div>
      <div v-if="availableAssignees.length > 0" class="input-group">
        <label>Assign To:</label>
        <div class="assignee-list">
          <div v-for="assignee in availableAssignees" :key="assignee.id" class="assignee-item">
            <span>{{ assignee.prename }}, {{ assignee.name }}</span>
            <input type="checkbox" :value="assignee.id" v-model="selectedAssignees" />
          </div>
        </div>
      </div>
      <div v-else>
        <p>
          No Assignees available to assign.
          <a href="#" @click.prevent="navigateToCreateAssignee">Click here</a> to create an
          assignee.
        </p>
      </div>
      <div class="input-group-checkbox">
        <label for="finished" class="checkbox-label">
          <input id="finished" type="checkbox" v-model="todo.finished" class="checkbox-input" />
          Finished
        </label>
      </div>
      <div class="button-container">
        <button type="button" class="go-back-btn" @click="goBack">Go Back</button>
        <button type="submit" class="submit-btn">Update</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, Toast } from '@/ts/toasts'
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons'
import config from '@/config'
import '/src/assets/styling/detail.css'

const route = useRoute()
const router = useRouter()
const todo = ref<any>(null)
const availableAssignees = ref<{ id: number; name: string; prename: string }[]>([])
const selectedAssignees = ref<number[]>([])
const dueDate = ref<string>('')
const dueTime = ref<string>('00:00')

defineProps({
  id: {
    type: String,
    required: true
  }
})

/**
 * Fetches the ToDo item by id from the API
 * populates the available assignees and formats the due date and time
 */
async function fetchToDo() {
  try {
    const response = await fetch(`${config.apiBaseUrl}/todos/${route.params.id}`)
    const data = await response.json()
    todo.value = data
    selectedAssignees.value = data.assigneeList.map((assignee: { id: number }) => assignee.id)
    if (data.dueDate) {
      const dateObj = new Date(data.dueDate)
      dueDate.value = dateObj.toISOString().split('T')[0]
      dueTime.value = dateObj.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })
    }
  } catch (error) {
    showToast(new Toast('Error', (error as Error).message, 'error', faXmark, 5))
  }
}

/**
 * Loads the list of available assignees from the API with a GET request
 */
async function loadAssignees() {
  try {
    const response = await fetch(`${config.apiBaseUrl}/assignees`)
    const data = await response.json()
    availableAssignees.value = data
  } catch (error) {
    showToast(new Toast('Error', (error as Error).message, 'error', faXmark, 5))
  }
}

/**
 * Combines the provided date and time into a single timestamp
 * @param date - The due date as a string (yyyy-mm-dd)
 * @param time - The due time as a string (hh:mm).
 * @returns A timestamp in ms, or null if the date is not provided.
 */
function combineDateAndTime(date: string, time: string): number | null {
  if (!date) return null
  const timeToUse = time || '00:00'
  const dateTimeString = `${date}T${timeToUse}:00`
  const combinedDate = new Date(dateTimeString)
  return combinedDate.getTime()
}

/**
 * Updates the ToDo item with new values by sending a PUT request to the API
 */
async function updateToDo() {
  const updatedToDo = {
    ...todo.value,
    assigneeIdList: selectedAssignees.value,
    dueDate: combineDateAndTime(dueDate.value, dueTime.value)
  }

  try {
    const response = await fetch(`${config.apiBaseUrl}/todos/${todo.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(updatedToDo)
    })
    if (!response.ok) throw new Error('Failed to update ToDo.')
    showToast(new Toast('Success', 'ToDo updated!', 'success', faCheck, 5))
  } catch (error) {
    showToast(new Toast('Error', (error as Error).message, 'error', faXmark, 5))
  }
}

/**
 * Navigates back to the previous page
 */
function goBack() {
  router.back()
}

/**
 * Navigates to the create-assignee page
 */
function navigateToCreateAssignee() {
  router.push('/create-assignee')
}

onMounted(() => {
  fetchToDo()
  loadAssignees()
})
</script>
