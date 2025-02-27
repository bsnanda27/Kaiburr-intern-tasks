import React, { useState } from "react";
import { Modal, Form, Input, Button, message } from "antd";
import { createTask } from "../api/api";

interface Props {
  visible: boolean;
  onClose: () => void;
}

const TaskCreateModal: React.FC<Props> = ({ visible, onClose }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleCreate = async () => {
    setLoading(true);
    try {
      const values = await form.validateFields();
      await createTask(values);
      message.success("Task created successfully!");
      form.resetFields();
      onClose();
    } catch (error) {
      message.error("Failed to create task");
    }
    setLoading(false);
  };

  return (
    <Modal open={visible} title="Create New Task" onCancel={onClose} footer={null}>
      <Form form={form} layout="vertical">
        <Form.Item name="id" label="Task ID" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="name" label="Task Name" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="owner" label="Owner" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="command" label="Command" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Button type="primary" onClick={handleCreate} loading={loading}>
          Create Task
        </Button>
      </Form>
    </Modal>
  );
};

export default TaskCreateModal;
