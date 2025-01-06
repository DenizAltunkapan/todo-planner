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
            >created: {{ new Date(todo.createdDate).toLocaleDateString() }}</span
          >
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
              | {{ todo.category.charAt(0).toUpperCase() + todo.category.slice(1) }}
            </div>
            <span class="todo-created-date"
              >created: {{ new Date(todo.createdDate).toLocaleDateString() }}</span
            >
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
    <FontAwesomeIcon :icon="faDownload" class="icon" /> Download CSV
  </button>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faCheck, faXmark, faTrash, faEdit, faDownload } from '@fortawesome/free-solid-svg-icons'
import '/src/assets/styling/todoList.css'
import {
  toggleToDoUnfinished,
  toggleToDoFinished,
  confirmDelete,
  downloadCSV,
  fetchToDos,
  todos
} from '@/ts/todos-view'

const filterTitle = ref('')
const filterDate = ref('')
const sortKey = ref<'title' | 'dueDate'>('dueDate')
const category = ref<'all' | 'private' | 'work'>('all')
const sortDirection = ref<'asc' | 'desc'>('asc')
const router = useRouter()

/**
 * Computed property to filter and sort ToDos
 * @type {import('vue').ComputedRef<ToDo[]>}
 */
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

  if (category.value !== 'all') {
    filtered = filtered.filter((todo) => todo.category === category.value)
  }

  return filtered
})

/**
 * Clears all applied filters and resets them to their default values.
 */
function clearFilters() {
  filterTitle.value = ''
  filterDate.value = ''
  sortKey.value = 'dueDate'
  sortDirection.value = 'asc'
  category.value = 'all'
}

/**
 * Navigates to the details page of a specific ToDo
 * @param {number} id - The ID of the ToDo to navigate to
 */
function navigateToDetails(id: number) {
  router.push(`/todos/${id}`)
}

/**
 * Navigates to the details page of a specific assignee
 * @param {number} id - The ID of the assignee to navigate to
 */
function navigateToAssignee(id: number) {
  if (!id) return
  router.push(`/assignees/${id}`)
}

/**
 * Navigates to the page for creating a new ToDo
 */
function navigateToCreateTodo() {
  router.push('/create-todo')
}

onMounted(() => {
  fetchToDos()
})
</script>
