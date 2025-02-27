import React, { useEffect } from "react";
import { Modal, Form, Input, Button, message } from "antd";
import { updateTask } from "../api/api";

interface Props {
  task: any;
  visible: boolean;
  onClose: () => void;
  onUpdate: () => void; // ✅ Reload tasks after updating
}

const TaskEditModal: React.FC<Props> = ({ task, visible, onClose, onUpdate }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (task) {
      form.setFieldsValue(task);
    }
  }, [task]);

  const handleUpdate = async () => {
    try {
      const values = await form.validateFields();
      await updateTask(task.id, values); // ✅ Send ID to backend
      message.success("Task updated successfully!");
      onClose();
      onUpdate(); // Reload tasks
    } catch (error) {
      message.error("Failed to update task");
    }
  };

  return (
    <Modal open={visible} title="Edit Task" onCancel={onClose} footer={null}>
      <Form form={form} layout="vertical">
        <Form.Item name="name" label="Task Name" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="owner" label="Owner" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="command" label="Command" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Button type="primary" onClick={handleUpdate}>
          Update Task
        </Button>
      </Form>
    </Modal>
  );
};

export default TaskEditModal;
