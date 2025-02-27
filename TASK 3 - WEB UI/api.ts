import axios from "axios";

const API_BASE_URL = "http://localhost:8080";

const api = axios.create({
  baseURL: API_BASE_URL,
});


// Define Task interface
interface Task {
    id: string;
    name: string;
    owner: string;
    command: string;
    taskExecutions: TaskExecution[];
  }
  
  interface TaskExecution {
    startTime: string;
    endTime: string;
    output: string;
  }
  
  export const getTasks = () => api.get("/tasks");
  export const getTaskById = (id: string) => api.get(`/tasks/${id}`);
  export const createTask = (task: any) => api.put(`/tasks`, task);
  export const updateTask = (id: string, task: any) => api.put(`/tasks/${id}`, task);
  export const deleteTask = (id: string) => api.delete(`/tasks/${id}`);
  export const runTask = (id: string) => api.put(`/tasks/${id}/execute`);
  export const getExecutions = (id: string) => api.get(`/tasks/${id}/executions`);
