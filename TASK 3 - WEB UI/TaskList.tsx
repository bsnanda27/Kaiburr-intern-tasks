import React, { useEffect, useState } from "react";
import { Table, Button, Space, Input, Form, message } from "antd";
import { getTasks, deleteTask, runTask, createTask, updateTask } from "../api/api";
import TaskEditModal from "./TaskEditModal";
import TaskExecutions from "./TaskExecutions";

const TaskList: React.FC = () => {
  const [tasks, setTasks] = useState<any[]>([]);
  const [search, setSearch] = useState("");
  const [editingTask, setEditingTask] = useState<any | null>(null);
  const [selectedTask, setSelectedTask] = useState<any | null>(null);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    loadTasks();
  }, [search]);

  const loadTasks = async () => {
    try {
      const { data } = await getTasks();
      setTasks(
        data.filter((task: any) =>
          task.name.toLowerCase().includes(search.toLowerCase())
        )
      );
    } catch (error) {
      message.error("Failed to load tasks");
    }
  };

  const handleCreateTask = async () => {
    setLoading(true);
    try {
      const values = await form.validateFields();
      await createTask(values); // No ID passed; backend generates it
      message.success("Task created successfully!");
      form.resetFields();
      loadTasks();
    } catch (error) {
      message.error("Failed to create task");
    }
    setLoading(false);
  };

  return (
    <div style={{ padding: 20 }}>
      {/* Create Task Form */}
      <h2>Create New Task</h2>
      <Form form={form} layout="inline">
        <Form.Item name="name" label="Task Name" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="owner" label="Owner" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="command" label="Command" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Button type="primary" onClick={handleCreateTask} loading={loading}>
          Create Task
        </Button>
      </Form>

      <h2>Task List</h2>
      <Input
        placeholder="Search tasks..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{ marginBottom: 16 }}
      />

      <Table
        columns={[
          { title: "Name", dataIndex: "name" },
          { title: "Owner", dataIndex: "owner" },
          { title: "Command", dataIndex: "command" },
          {
            title: "Actions",
            render: (_: any, record: any) => (
              <Space>
                <Button onClick={() => runTask(record.id)}>Run</Button>
                <Button type="primary" onClick={() => setEditingTask(record)}>
                  Edit
                </Button>
                <Button danger onClick={() => deleteTask(record.id)}>
                  Delete
                </Button>
                <Button onClick={() => setSelectedTask(record)}>Executions</Button>
              </Space>
            ),
          },
        ]}
        dataSource={tasks}
        rowKey="id"
      />

      <TaskEditModal
        task={editingTask}
        visible={!!editingTask}
        onClose={() => setEditingTask(null)}
        onUpdate={loadTasks} // Refresh after update
      />
      <TaskExecutions
        task={selectedTask}
        visible={!!selectedTask}
        onClose={() => setSelectedTask(null)}
      />
    </div>
  );
};

export default TaskList;
