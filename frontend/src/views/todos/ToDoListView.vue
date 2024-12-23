<template>
  <h1 class="title">ToDo List</h1>

  <div class="filter-container">
    <input v-model="filterTitle" placeholder="Filter by title" class="filter-input" />
    <select v-model="sortKey" class="filter-select">
      <option value="dueDate">Due Date</option>
      <option value="title">Title</option>
    </select>
    <select v-model="sortDirection" class="filter-select">
      <option value="asc">Ascending</option>
      <option value="desc">Descending</option>
    </select>
    <select v-model="category" class="filter-select">
      <option value="all">All</option>
      <option value="private">Private</option>
      <option value="work">Work</option>
    </select>
    <input v-model="filterDate" type="date" class="filter-input" placeholder="Filter by Due Date" />
    <button @click="clearFilters" class="clear-btn">Clear Filters</button>
  </div>

  <h2>Open ToDos</h2>
  <div v-if="filteredToDos.filter((todo) => !todo.finished).length === 0" class="empty-message">
    <p>No open ToDos...</p>
    <p><a href="#" @click.prevent="navigateToCreateTodo">Click here</a> to create a ToDo.</p>
  </div>
  <ul class="todo-list">
    <li
      v-for="todo in filteredToDos.filter((todo) => !todo.finished)"
      :key="todo.id"
      class="todo-item"
    >
      <div class="todo-content">
        <div class="todo-header">
          <div>
            <strong>{{ todo.title }}</strong>
            | {{ todo.category.charAt(0).toUpperCase() + todo.category.slice(1) }}
          </div>
          <span class="todo-created-date"
            >created: {{ new Date(todo.createdDate).toLocaleDateString() }}</span>
        </div>
        <p class="todo-description">{{ todo.description }}</p>
        <details class="assignees-details" v-if="todo.assigneeList && todo.assigneeList.length">
          <summary>View Assignees</summary>
          <div class="assignees-container">
            <ul>
              <li v-for="assignee in todo.assigneeList" :key="assignee.id" class="assignee-item">
                <span @click="navigateToAssignee(assignee.id)">
                  {{ assignee.prename }} {{ assignee.name }}
                </span>
              </li>
            </ul>
          </div>
        </details>
      </div>
      <div class="todo-actions">
        <div class="todo-due-date">
          {{ new Date(todo.dueDate || todo.createdDate).toLocaleDateString() }} |
          {{ new Date(todo.dueDate || todo.createdDate).toLocaleTimeString() }}
        </div>
        <div class="todo-buttons-row">
          <button @click="toggleToDoFinished(todo)" class="todo-btn mark-finished-btn">
            <FontAwesomeIcon :icon="faCheck" class="icon" /> Mark as Finished
          </button>
          <button @click="navigateToDetails(todo.id)" class="todo-btn details-btn">
            <FontAwesomeIcon :icon="faEdit" class="icon" /> Details
          </button>
          <button @click="confirmDelete(todo)" class="todo-btn delete-btn">
            <FontAwesomeIcon :icon="faTrash" class="icon" /> Delete
          </button>
        </div>
      </div>
    </li>
  </ul>

  <h2>Finished ToDos</h2>
  <details
    v-if="filteredToDos.filter((todo) => todo.finished).length > 0"
    class="finished-container"
  >
    <summary>Show Finished ToDos</summary>
    <ul class="todo-list finished-list">
      <li
        v-for="todo in filteredToDos.filter((todo) => todo.finished)"
        :key="todo.id"
        class="todo-item"
      >
        <div class="todo-content">
          <div class="todo-header">
            <div>
              <strong>{{ todo.title }}</strong>
            |   {{ todo.category.charAt(0).toUpperCase() + todo.category.slice(1) }}
            </div>
          <span class="todo-created-date"
            >created: {{ new Date(todo.createdDate).toLocaleDateString() }}</span>
          </div>
          <p class="todo-description">{{ todo.description }}</p>
          <details class="assignees-details" v-if="todo.assigneeList && todo.assigneeList.length">
            <summary>View Assignees</summary>
            <div class="assignees-container">
              <ul>
                <li v-for="assignee in todo.assigneeList" :key="assignee.id" class="assignee-item">
                  <span @click="navigateToAssignee(assignee.id)">
                    {{ assignee.prename }} {{ assignee.name }}
                  </span>
                </li>
              </ul>
            </div>
          </details>
        </div>
        <div class="todo-actions">
          <div class="todo-due-date">
            {{ new Date(todo.dueDate || todo.createdDate).toLocaleDateString() }} |
            {{ new Date(todo.dueDate || todo.createdDate).toLocaleTimeString() }}
          </div>
          <div class="finished-date">
            Finished:
            {{ new Date(todo.finishedDate || todo.createdDate).toLocaleDateString() }} |
            {{ new Date(todo.finishedDate || todo.createdDate).toLocaleTimeString() }}
          </div>
          <div class="todo-buttons-row">
            <button @click="toggleToDoUnfinished(todo)" class="todo-btn unfinished-btn">
              <FontAwesomeIcon :icon="faXmark" class="icon" /> Mark as Unfinished
            </button>
            <button @click="navigateToDetails(todo.id)" class="todo-btn details-btn">
              <FontAwesomeIcon :icon="faEdit" class="icon" /> Details
            </button>
            <button @click="confirmDelete(todo)" class="todo-btn delete-btn">
              <FontAwesomeIcon :icon="faTrash" class="icon" /> Delete
            </button>
          </div>
        </div>
      </li>
    </ul>
  </details>
  <div v-else class="empty-message">
    <p>No finished ToDos yet.</p>
  </div>
  <button class="download-btn" @click="downloadCSV">
    <FontAwesomeIcon :icon="faDownload" class="icon" />  Download CSV
  </button>

</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, Toast } from '@/ts/toasts'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faCheck, faXmark, faTrash, faEdit, faDownload} from '@fortawesome/free-solid-svg-icons'
import config from '@/config'
import '/src/assets/todoList.css'
import { saveAs } from 'file-saver';

interface Assignee {
  id: number
  name: string
  prename: string
  email: string
}

interface ToDo {
  id: number
  title: string
  description: string
  finished: boolean
  finishedDate?: number
  createdDate: number
  dueDate?: number
  assigneeList: Assignee[]
  category: string
}

const todos = ref<ToDo[]>([])
const filterTitle = ref('')
const filterDate = ref('')
const sortKey = ref<'title' | 'dueDate'>('dueDate')
const category = ref<'all' | 'private' | 'work'>('all')
const sortDirection = ref<'asc' | 'desc'>('asc')

const router = useRouter()

async function fetchToDos() {
  try {
    const response = await fetch(`${config.apiBaseUrl}/todos`)
    if (!response.ok) throw new Error('Failed to fetch ToDos')
    todos.value = await response.json()
  } catch (error) {
    showToast(
      new Toast('Error', `Could not load ToDos: ${(error as Error).message}`, 'error', faXmark, 5)
    )
  }
}

async function toggleToDoUnfinished(todo: ToDo) {
  const updatedTodo = {
    ...todo,
    finished: false,
    assigneeIdList: todo.assigneeList.map((a) => a.id)
  }

  try {
    const response = await fetch(`${config.apiBaseUrl}/todos/${todo.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedTodo)
    })

    if (!response.ok) {
      throw new Error('Failed to update ToDo')
    }

    const updatedData = await response.json()
    Object.assign(todo, updatedData)
    todo.finishedDate = undefined

    showToast(new Toast('Success', 'ToDo marked as unfinished!', 'success', faCheck, 5))
  } catch (error) {
    showToast(new Toast('Error', (error as Error).message, 'error', faXmark, 5))
  }
}

const filteredToDos = computed(() => {
  let filtered = todos.value.filter((todo) =>
    todo.title.toLowerCase().includes(filterTitle.value.toLowerCase())
  )

  if (filterDate.value) {
    const selectedDate = new Date(filterDate.value).toDateString()
    filtered = filtered.filter(
      (todo) => new Date(todo.dueDate || todo.createdDate).toDateString() === selectedDate
    )
  }

  filtered.sort((a, b) => {
    const direction = sortDirection.value === 'asc' ? 1 : -1
    return (
      direction *
      (sortKey.value === 'title'
        ? a.title.localeCompare(b.title)
        : (a.dueDate || 0) - (b.dueDate || 0))
    )
  })

  if (category.value !== 'all'){
    filtered = filtered.filter((todo) => todo.category === category.value);
  }

  return filtered
})

onMounted(() => {
  fetchToDos()
})

function clearFilters() {
  filterTitle.value = ''
  filterDate.value = ''
  sortKey.value = 'dueDate'
  sortDirection.value = 'asc'
  category.value = 'all'
}

function navigateToDetails(id: number) {
  router.push(`/todos/${id}`)
}

function navigateToAssignee(id: number) {
  if (!id) return
  router.push(`/assignees/${id}`)
}

function navigateToCreateTodo() {
  router.push('/create-todo')
}

async function toggleToDoFinished(todo: ToDo) {
  try {
    const updatedTodo = {
      ...todo,
      finished: true,
      assigneeIdList: todo.assigneeList.map((a) => a.id)
    }
    const response = await fetch(`${config.apiBaseUrl}/todos/${todo.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedTodo)
    })

    if (!response.ok) throw new Error('Failed to update ToDo')

    const updatedData = await response.json()

    Object.assign(todo, updatedData)

    showToast(new Toast('Success', 'ToDo marked as finished!', 'success', faCheck, 5))
  } catch (error) {
    showToast(new Toast('Error', (error as Error).message, 'error', faXmark, 5))
  }
}

async function confirmDelete(todo: ToDo) {
  const confirm = window.confirm('Are you sure you want to delete this ToDo?')
  if (!confirm) return

  try {
    const response = await fetch(`${config.apiBaseUrl}/todos/${todo.id}`, { method: 'DELETE' })
    if (!response.ok) throw new Error('Failed to delete ToDo')
    todos.value = todos.value.filter((t) => t.id !== todo.id)
    showToast(new Toast('Success', 'ToDo deleted!', 'success', faCheck, 5))
  } catch (error) {
    showToast(new Toast('Error', (error as Error).message, 'error', faXmark, 5))
  }
}

async function downloadCSV() {
  try {
    const response = await fetch(`${config.apiBaseUrl}/csv-downloads/todos`, {
      method: 'GET',
      headers: { 'Content-Type': 'text/csv' }
    });

    if (!response.ok) {
      throw new Error('Failed to download CSV');
    }

    const currentDate = new Date().toISOString().split('T')[0];
    const currentTime = new Date().toLocaleTimeString().replace(/:/g, '-').replace(/\s/g, '-');
    const fileName = `todos_${currentDate}_${currentTime}.csv`;

    const blob = await response.blob();
    saveAs(blob, fileName);

    showToast(new Toast('Success', 'Ready to download the CSV!', 'success', faCheck, 5));
  } catch (error) {
    showToast(new Toast('Error', (error as Error).message, 'error', faXmark, 5));
  }
}

</script>
