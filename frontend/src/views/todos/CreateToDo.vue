<template>
  <h1>Create ToDo</h1>
  <form @submit.prevent="createToDo" class="form-container">
    <div class="input-group">
      <label>Title:</label>
      <input v-model="title" class="input-field" placeholder="Enter a title" required />
    </div>
    <div class="input-group">
      <label>Description:</label>
      <textarea
        v-model="description"
        class="input-field"
        placeholder="Enter a description"
        maxlength="255"
      ></textarea>
      <small>{{ description.length }}/255 characters</small>
    </div>
    <div class="input-group">
      <label>Due Date:</label>
      <input v-model="dueDate" type="date" class="input-field" />
      <input v-model="dueTime" type="time" class="input-field" />
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
        <a href="#" @click.prevent="navigateToCreateAssignee">Click here</a> to create an assignee.
      </p>
    </div>
    <button type="submit" class="submit-btn">Create</button>
  </form>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast, Toast } from '@/ts/toasts'
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons'
import config from '@/config'
import { useRouter } from 'vue-router'

const title = ref<string>('')
const description = ref<string>('')
const dueDate = ref<string>('')
const dueTime = ref<string>('00:00')
const selectedAssignees = ref<number[]>([])
const availableAssignees = ref<Array<{ id: number; name: string; prename: string }>>([])

const router = useRouter()

/**
 * Fetches the list of available assignees from the API
 * If an error occurs, a toast is displayed to notify the user
 */
async function loadAssignees() {
  try {
    const response = await fetch(`${config.apiBaseUrl}/assignees`)
    if (!response.ok) throw new Error('Failed to fetch Assignees.')
    availableAssignees.value = await response.json()
  } catch (error) {
    const err = error as Error
    showToast(new Toast('Error', err.message, 'error', faXmark, 5))
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
 * Creates a new ToDo by sending a POST request to the API.
 * Validates that the title is filled in, and sets default values for other fields.
 * Displays a toast on success or failure, and resets the form after successful creation
 */
function createToDo() {
  if (!title.value.trim()) {
    showToast(new Toast('Validation Error', 'Title is required.', 'error', faXmark, 5))
    return
  }
  const now = new Date().getTime()
  const newToDo = {
    title: title.value,
    description: description.value || '',
    finished: false,
    assigneeIdList: selectedAssignees.value,
    createdDate: now,
    dueDate: combineDateAndTime(dueDate.value, dueTime.value) || now + 7 * 24 * 60 * 60 * 1000
  }
  fetch(`${config.apiBaseUrl}/todos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(newToDo)
  })
    .then((response) => {
      if (!response.ok) throw new Error('Failed to create ToDo.')
      return response.json()
    })
    .then(() => {
      showToast(new Toast('Success', 'ToDo created!', 'success', faCheck, 5))
      title.value = ''
      description.value = ''
      dueDate.value = ''
      dueTime.value = '00:00'
      selectedAssignees.value = []

      setTimeout(() => {
        router.push('/todos').then(() => router.go(0))
      }, 500)
    })
    .catch((error) => showToast(new Toast('Error', error.message, 'error', faXmark, 5)))
}

/**
 * Navigates to the "CreateAssignee" page to create a new assignee.
 */
function navigateToCreateAssignee() {
  router.push({ name: 'CreateAssignee' })
}

onMounted(loadAssignees)
</script>
