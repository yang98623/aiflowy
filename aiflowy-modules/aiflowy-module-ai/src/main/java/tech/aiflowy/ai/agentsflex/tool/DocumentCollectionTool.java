package tech.aiflowy.ai.agentsflex.tool;

import com.agentsflex.core.document.Document;
import com.agentsflex.core.model.chat.tool.BaseTool;
import com.agentsflex.core.model.chat.tool.Parameter;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.service.DocumentCollectionService;
import tech.aiflowy.common.util.SpringContextUtil;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class DocumentCollectionTool extends BaseTool {

    private BigInteger knowledgeId;

    public DocumentCollectionTool() {
    }

    public DocumentCollectionTool(DocumentCollection documentCollection, boolean needEnglishName) {
        this.knowledgeId = documentCollection.getId();
        if (needEnglishName) {
            this.name = documentCollection.getEnglishName();
        } else {
            this.name = documentCollection.getTitle();
        }
        this.description = documentCollection.getDescription();
        this.parameters = getDefaultParameters();
    }


    public Parameter[] getDefaultParameters() {
        Parameter parameter = new Parameter();
        parameter.setName("input");
        parameter.setDescription("要查询的相关知识");
        parameter.setType("string");
        parameter.setRequired(true);
        return new Parameter[]{parameter};
    }

    public BigInteger getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(BigInteger knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    @Override
    public Object invoke(Map<String, Object> argsMap) {
        if (argsMap == null) {
            return "未查询到相关知识";
        }
        DocumentCollectionService knowledgeService = SpringContextUtil.getBean(DocumentCollectionService.class);
        List<Document> documents = knowledgeService.search(this.knowledgeId, (String) argsMap.get("input"));

        StringBuilder sb = new StringBuilder();
        if (documents != null) {
            for (Document document : documents) {
                sb.append(document.getContent());
            }
        }
        return sb.toString();
    }


}
