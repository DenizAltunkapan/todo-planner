import { ref } from 'vue'
import { showToast, Toast } from '@/ts/toasts'
import config from '@/config'
import { saveAs } from 'file-saver'
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons'

export interface Assignee {
  id: number
  name: string
  prename: string
  email: string
}

export interface ToDo {
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

export const todos = ref<ToDo[]>([])

/**
 * Fetches all ToDos from the server
 * @async
 * @function fetchToDos
 * @returns {Promise<void>} Resolves when the ToDos are successfully fetched.
 */
export async function fetchToDos() {
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

/**
 * Marks a ToDo as unfinished.
 * @async
 * @function toggleToDoUnfinished
 * @param {ToDo} todo - The ToDo to update.
 * @returns {Promise<void>} Resolves when the ToDo is successfully updated.
 */
export async function toggleToDoUnfinished(todo: ToDo) {
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

/**
 * Marks a ToDo as finished.
 * @async
 * @function toggleToDoFinished
 * @param {ToDo} todo - The ToDo to update.
 * @returns {Promise<void>} Resolves when the ToDo is successfully updated.
 */
export async function toggleToDoFinished(todo: ToDo) {
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

/**
 * Deletes a ToDo after the user confirms it
 * @async
 * @function confirmDelete
 * @param {ToDo} todo - The ToDo to delete.
 * @returns {Promise<void>} Resolves when the ToDo is successfully deleted.
 */
export async function confirmDelete(todo: ToDo) {
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

/**
 * Downloads the all ToDos as a CSV file.
 * @async
 * @function downloadCSV
 * @returns {Promise<void>} Resolves when the CSV file is ready for download.
 */
export async function downloadCSV() {
  try {
    const response = await fetch(`${config.apiBaseUrl}/csv-downloads/todos`, {
      method: 'GET',
      headers: { 'Content-Type': 'text/csv' }
    })

    if (!response.ok) {
      throw new Error('Failed to download CSV')
    }

    const currentDate = new Date().toISOString().split('T')[0]
    const currentTime = new Date().toLocaleTimeString().replace(/:/g, '-').replace(/\s/g, '-')
    const fileName = `todos_${currentDate}_${currentTime}.csv`

    const blob = await response.blob()
    saveAs(blob, fileName)

    showToast(new Toast('Success', 'Ready to download the CSV!', 'success', faCheck, 5))
  } catch (error) {
    showToast(new Toast('Error', (error as Error).message, 'error', faXmark, 5))
  }
}
