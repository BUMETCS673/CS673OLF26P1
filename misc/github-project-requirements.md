
# 📋 GitHub Project Requirements Cheat Sheet

## 1. Structure Overview

| Hierarchy | GitHub Element | Label / Structure | Scope / Description |
| --- | --- | --- | --- |
| **Epic** | Issue | `type: epic` | High-level feature or capability. Contains tracked User Stories in a Markdown task list. |
| **User Story** | Issue | `type: user-story` | A single piece of user functionality. Contains acceptance criteria and subtasks. |
| **Task** | Sub-Issue / Tasklist | `- [ ] Task name` | Technical step required to implement a User Story. |

---

## 2. Issue Templates

### 🔴 Epic Template

> **Title:** `[EPIC] <Short Title>`
> 
> **Labels:** `type: epic`

```markdown
## Overview
Brief description of the overall feature set, business goals, and target metrics.

## Scope
- What is IN scope
- What is OUT of scope

## User Stories
- [ ] #001 <Title of User Story 1>
- [ ] #002 <Title of User Story 2>
- [ ] #003 <Title of User Story 3>

## Notes & Design Links
- [Figma Wireframes](https://example.com)
- API Spec / Documentation
```

---

### 🟡 User Story Template

> **Title:** `[STORY] <As a... I want...>` 
> 
> **Labels:** `type: user-story`

```markdown
## User Story
**As a** <type of user>,  
**I want to** <perform an action>,  
**So that** <achieve a specific value/outcome>.

## Acceptance Criteria
- [ ] Scenario 1: Given <context>, when <action>, then <outcome>
- [ ] Scenario 2: Given <context>, when <action>, then <outcome>

## Technical Tasks
- [ ] Task 1: Draft backend database schema
- [ ] Task 2: Create API endpoint
- [ ] Task 3: Build UI component
- [ ] Task 4: Write unit and integration tests

## Release / Milestone
- Assigned Milestone: `iterationX`
```

---

## 3. How to Create Tasks from Tasklists (Step-by-Step)

When a User Story becomes large or requires multiple engineers, convert its tasklist into separate tracked GitHub Issues.

### Step-by-Step Workflow:

```
1. Write Tasklist in Story  ──>  2. Hover over Task  ──>  3. Click "Convert to Issue"
```

1. **Write the Markdown List:** 
   * Open the User Story issue and write your tasklist using `- [ ]` syntax:
```markdown
## Technical Tasks
- [ ] Implement database schema for users
- [ ] Add POST /api/v1/users endpoint
- [ ] Add input validation logic
```

2. **Save the Issue Description.**


3. **Hover & Convert:**
   * Hover your cursor over the specific task item (`- [ ] Implement database...`).
   * A small icon with a circle and arrow (**"Convert to issue"**) will appear on the far right of that row.
   * Click **Convert to issue**.


4. **Result:**
   * GitHub will automatically convert that item into a full GitHub Issue.
   * A link to the new Task Issue will replace the text in the original User Story.
   * GitHub will display a progress counter (e.g., `1 of 4 tasks`) at the top of the User Story.

---

## 4. Best Practices 

* **Always Link Upwards:** When creating a User Story, paste its `#number` into the parent Epic's tasklist. GitHub will automatically create a bi-directional link between them.
* **Don't Over-Convert Tasks:** Keep tasks inside the User Story as simple checklist items *unless* they need to be assigned to a different developer, tracked across different sprints, or require detailed discussion.
* **Set Milestones at the Story Level:** Assign `Milestone` (e.g., `iterationX`) to User Stories rather than Epics. This provides accurate progress bars on the release dashboard.