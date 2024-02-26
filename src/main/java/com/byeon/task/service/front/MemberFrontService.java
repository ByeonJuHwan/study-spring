package com.byeon.task.service.front;

import com.byeon.task.domain.entity.Member;
import com.byeon.task.domain.result.MemberResult;
import com.byeon.task.domain.result.RestResult;
import com.byeon.task.dto.MemberJoinDto;
import com.byeon.task.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberFrontService {
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    public RestResult members() {

        List<Member> members = memberService.getAll();

        List<MemberResult> memberResults = new ArrayList<>();
        for (Member member : members) {
            memberResults.add(modelMapper.map(member, MemberResult.class));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("members", memberResults);
        return new RestResult(data);
    }

    public RestResult join(MemberJoinDto memberJoinDto) {
        return null;
    }
}

