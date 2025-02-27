import React, { useState } from "react";
import { Form, Input, Button, message } from "antd";
import { createTask } from "../api/api";

const TaskForm: React.FC = () => {
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: any) => {
    setLoading(true);
    try {
      await createTask(values);
      message.success("Task created successfully!");
    } catch (error) {
      message.error("Failed to create task");
    }
    setLoading(false);
  };

  return (
    <Form layout="vertical" onFinish={onFinish}>
      <Form.Item name="name" label="Task Name" rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Form.Item name="owner" label="Owner" rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Form.Item name="command" label="Command" rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Button type="primary" htmlType="submit" loading={loading}>
        Create Task
      </Button>
    </Form>
  );
};

export default TaskForm;
